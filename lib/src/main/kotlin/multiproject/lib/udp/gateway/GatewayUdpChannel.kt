package multiproject.lib.udp.gateway

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.command.CommitDto
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.exceptions.gateway.ResolveError
import multiproject.lib.request.Request
import multiproject.lib.sd.GatewayBalancer
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.server.router.CommandSyncType
import multiproject.lib.utils.LogLevel
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.time.ZonedDateTime
import java.util.*

class GatewayUdpChannel: UdpChannel() {
    private var pendingRequests = Collections.synchronizedMap(mutableMapOf<Pair<Long, InetSocketAddress>, Pair<ConnectedServer, Request>>())
    private var commits: MutableList<CommitDto> = mutableListOf()
    private val gatewayScope = CoroutineScope(Job())
    val clientRequestsChannel: Channel<Pair<SocketAddress, Request>> = Channel(capacity = Channel.BUFFERED)
    val failedRequestsChannel: Channel<Pair<ResolveError, Request>> = Channel(capacity = Channel.BUFFERED)

    private var syncState = SyncState()
    private var syncHelper: SyncHelper = SyncHelper()

    data class SyncState(
        var blocked: Boolean = false,
        var suspendedRequests: MutableList<Request> = mutableListOf(),
        var initiator: Request? = null,
    )

    private sealed class GatewayCommand() {
        class GetCommits(val response: CompletableDeferred<MutableList<CommitDto>> = CompletableDeferred()): GatewayCommand()
        class GetSyncState(val response: CompletableDeferred<SyncState> = CompletableDeferred()): GatewayCommand()
        class SetSyncState(val syncState: SyncState): GatewayCommand()
        class GetPendingRequests(val response: CompletableDeferred<MutableMap<Pair<Long, InetSocketAddress>, Pair<ConnectedServer, Request>>> = CompletableDeferred()): GatewayCommand()
        class GetOnePendingRequest(val key: Pair<Long, InetSocketAddress>, val response: CompletableDeferred<Pair<ConnectedServer, Request>?> = CompletableDeferred()): GatewayCommand()
        class ClearPending(val request: Request, val response: CompletableDeferred<Boolean> = CompletableDeferred()): GatewayCommand()
        class IsPendingClient(val request: Request, val response: CompletableDeferred<Boolean> = CompletableDeferred()): GatewayCommand()
        class RemovePendingRequest(val key: Pair<Long, InetSocketAddress>): GatewayCommand()
        class AddCommits(val commits: MutableList<CommitDto>): GatewayCommand()
        class ClearCommits: GatewayCommand()
        class CommitsGetAndClear(val response: CompletableDeferred<MutableList<CommitDto>> = CompletableDeferred()): GatewayCommand()
        class GetServers(val response: CompletableDeferred<MutableList<ConnectedServer>> = CompletableDeferred()): GatewayCommand()
        class AddServer(val address: ConnectedServer): GatewayCommand()
        class FilterServers: GatewayCommand()
        class UnblockServers(val address: InetSocketAddress): GatewayCommand()
        class SendThrough(val initiator: Request, val updateWith: Request.() -> Unit): GatewayCommand()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private val gatewayActor = gatewayScope.actor<GatewayCommand>(capacity = Channel.BUFFERED) {
        for (command in this) {
            if (!(
//                    command is GatewayCommand.GetPendingRequests ||
//                    command is GatewayCommand.GetServers ||
//                    command is GatewayCommand.FilterServers ||
                    command is GatewayCommand.GetSyncState
                )
                )
                logger(LogLevel.DEBUG, "Command coming: ${command.javaClass.simpleName}")

            when (command) {
                is GatewayCommand.GetCommits -> run {
                    command.response.complete(commits)
                }
                is GatewayCommand.GetSyncState -> run {
                    command.response.complete(syncState)
                }
                is GatewayCommand.SetSyncState -> run {
                    syncState = command.syncState
                }
                is GatewayCommand.GetPendingRequests -> run {
                    command.response.complete(pendingRequests)
                }
                is GatewayCommand.GetOnePendingRequest -> run {
                    command.response.complete(pendingRequests[command.key])
                }
                is GatewayCommand.ClearPending -> run {
                    val key = Pair(command.request.getHeader("id").toString().toLong(), command.request.getFrom())
                    command.response.complete(if (pendingRequests.containsKey(key)) {
                        pendingRequests.remove(key)
                        true
                    } else false)
                }
                is GatewayCommand.IsPendingClient ->
                    command.response.complete(run {
                        pendingRequests.forEach {
                            if (it.key.second == command.request.getFrom())
                                return@run true
                        }
                        return@run false
                    })
                is GatewayCommand.RemovePendingRequest -> run {
                    pendingRequests.remove(command.key)
                }
                is GatewayCommand.AddCommits -> run {
                    commits.addAll(command.commits)
                }
                is GatewayCommand.ClearCommits -> run {
                    commits = mutableListOf()
                }
                is GatewayCommand.CommitsGetAndClear -> run {
                    commits.clear()
                    command.response.complete(commits)
                }
                is GatewayCommand.GetServers -> run {
                    command.response.complete(servers)
                }
                is GatewayCommand.SendThrough -> run {
                    val initiator = command.initiator
                    val updateWith = command.updateWith

                    initiator.apply(updateWith)
                    sendThrough(initiator)
                }
                is GatewayCommand.FilterServers -> run {
                    val now = ZonedDateTime.now().toEpochSecond()
                    servers.replaceAll {
                        if (now - it.lastRequest >= UdpConfig.unavailableTimeout && it.pendingRequest >= 1 && !it.temporaryUnavailable.second)
                            it.temporaryUnavailable = Pair(now, true)

                        it
                    }
                    servers = servers.filter {
                            server -> if (server.temporaryUnavailable.second) {
                        now - server.temporaryUnavailable.first < UdpConfig.removeAfterUnavailableTimeout
                    } else true
                    }.toMutableList()
                }
                is GatewayCommand.UnblockServers -> run {
                    servers.find { it.address == command.address }?.apply {
                        temporaryUnavailable = Pair(0, false)
                        lastRequest = ZonedDateTime.now().toEpochSecond()
                        pendingRequest--
                    }
                }
                is GatewayCommand.AddServer -> run {
                    servers.add(command.address)
                }
            }
            if (!(
//                        command is GatewayCommand.GetPendingRequests ||
//                        command is GatewayCommand.GetServers ||
//                        command is GatewayCommand.FilterServers ||
                        command is GatewayCommand.GetSyncState))
                logger(LogLevel.DEBUG, "Command coming: ${command.javaClass.simpleName}")
        }
    }

    override fun addServer(address: ConnectedServer) {
        gatewayActor.trySend(GatewayCommand.AddServer(address))
    }

    private fun sendThrough(initiator: Request) {
        val now = ZonedDateTime.now().toEpochSecond()
        syncHelper.commits = mutableListOf()
        initiator.apply {
            this setHeader Pair("id", now)
            this setHeader Pair("sync", syncHelper)
        }
        val commandSyncType: CommandSyncType = initiator.getSyncType()

        if (commandSyncType.blocking) {
            if (commandSyncType.blockByArgument != null)
                syncHelper.removedInstances.add(initiator.data.inlineArguments[commandSyncType.blockByArgument].toString().toLong())
            if (commandSyncType.blockByDataValue != null)
                syncHelper.removedInstances.add(initiator.data.arguments[commandSyncType.blockByDataValue].toString().toLong())
        }

        val server = GatewayBalancer.getServer(servers, this@GatewayUdpChannel) ?: run {
            failedRequestsChannel.trySend(Pair(ResolveError(ResponseCode.CONNECTION_REFUSED), initiator))
            return@sendThrough
        }
        val serverAddress = server.address
        servers.find { serverAddress == it.address }!!.apply {
            pendingRequest++
            lastRequest = ZonedDateTime.now().toEpochSecond()
        }

        if (commandSyncType.sync || commits.size > 70) {
            syncState = SyncState(true, initiator = initiator)
            initiator.apply {
                this setSyncHelper (this.getSyncHelper().apply {
                    commits = this@GatewayUdpChannel.commits
                    servers = this@GatewayUdpChannel.servers.filter { it.address != serverAddress }.map { it.address }.toMutableList()
                })
                this setSyncType CommandSyncType(true)
            }
            logger(LogLevel.DEBUG, "Commits [${commits.size}]")
            commits = mutableListOf()
        }

        try {
            emit(serverAddress, initiator)
            pendingRequests[Pair(now, initiator.getFrom())] = (Pair(server, initiator))
        } catch (e: Exception) {
            logger(LogLevel.ERROR, "$e ${e.stackTraceToString()}")
            removeServer(serverAddress)
            ResponseDto(ResponseCode.CONNECTION_REFUSED, "Server unavailable")
            sendThrough(initiator)
        }
    }

    fun sendThrough(initiator: Request, updateWith: Request.() -> Unit) {
        val command = GatewayCommand.SendThrough(initiator, updateWith)
        gatewayActor.trySend(command)
    }

    suspend infix fun clearPending(request: Request): Boolean {
        val command = GatewayCommand.ClearPending(request)
        gatewayActor.send(command)
        return command.response.await()
    }

    suspend infix fun isPendingClient(request: Request): Boolean {
        val command = GatewayCommand.IsPendingClient(request)
        gatewayActor.send(command)
        return command.response.await()
    }

    fun unblockServers(address: InetSocketAddress) {
        gatewayActor.trySend(GatewayCommand.UnblockServers(address))
    }

    suspend fun syncState(): SyncState {
        val command = GatewayCommand.GetSyncState()
        gatewayActor.send(command)
        return command.response.await()
    }

    fun stopSync() {
        val res = gatewayActor.trySend(GatewayCommand.SetSyncState(SyncState(false)))
    }

    suspend fun getCommits(): MutableList<CommitDto> {
        val command = GatewayCommand.GetCommits()
        gatewayActor.send(command)
        return command.response.await()
    }

    fun addCommits(commits: MutableList<CommitDto>) {
        gatewayActor.trySend(GatewayCommand.AddCommits(commits))
    }

    override fun removeServer(address: InetSocketAddress) {
        servers.removeIf {
            it.address == address
        }
    }

    override suspend fun run() {
        val scope = CoroutineScope(Job())
        scope.launch {
            do {
                val now = ZonedDateTime.now().toEpochSecond()

                val getPendingCommand = GatewayCommand.GetPendingRequests()
                gatewayActor.send(getPendingCommand)
                val pendingRequests = getPendingCommand.response.await()

                val getServersCommand = GatewayCommand.GetServers()
                gatewayActor.send(getServersCommand)
                val servers = getServersCommand.response.await()

//                logger(LogLevel.DEBUG,"Pending requests: $pendingRequests")
//                logger(LogLevel.DEBUG, "Available servers: $servers")

                pendingRequests.forEach {
                        request -> if (now - request.key.first >= UdpConfig.holdRequestTimeout) {
                    logger(LogLevel.INFO,"Pending requests: $pendingRequests")
                    logger(LogLevel.INFO, "Available servers: $servers")
                    val req = request.value.second
                    try {
                        if (request.value.second.getSyncType().sync) {
                            gatewayActor.send(GatewayCommand.SetSyncState(SyncState(false)))
                        }
                        val sendAt = request.value.second.getHeader("sendAt")?.toString()?.toLong() ?: ZonedDateTime.now().toEpochSecond()

                        gatewayActor.send(GatewayCommand.RemovePendingRequest(request.key))

                        if (ZonedDateTime.now().toEpochSecond() - sendAt <= UdpConfig.holdRequestTimeout * 3)
                            sendThrough(req) {}
                        else
                            emit(request.value.second.getFrom(), req.apply {
                                response = ResponseDto(ResponseCode.INTERNAL_SERVER_ERROR, "Failed")
                            })
                        request.value.first.pendingRequest--
                        if (request.value.first.pendingRequest == 0) {
                            request.value.first.temporaryUnavailable = Pair(ZonedDateTime.now().toEpochSecond(), true)
                        }
                    } catch (e: ResolveError) {
                        val from = req.getFrom()
                        req.removeSystemHeaders()
                        when (e.code) {
                            ResponseCode.CONNECTION_REFUSED -> {
                                req.apply {
                                    response = ResponseDto(e.code, result = "Server unavailable. Connection refused.")
                                }
                            }
                            ResponseCode.BAD_REQUEST -> {
                                req.apply {
                                    response = ResponseDto(e.code, result = "Validation error!")
                                }
                            }
                            else -> {
                                req.apply {
                                    response = ResponseDto(ResponseCode.INTERNAL_SERVER_ERROR)
                                }
                            }
                        }
                        emit(from, req)
                    }
                    gatewayActor.send(GatewayCommand.RemovePendingRequest(request.key))
                }
                }
                gatewayActor.send(GatewayCommand.FilterServers())
                delay(5000L)
            } while (true)
        }
        coroutineScope {
            launch {
             super.run()
            }
        }
    }
}