package multiproject.lib.udp.gateway

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.command.CommitDto
import multiproject.lib.dto.request.RequestDirection
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
import java.time.ZonedDateTime
import java.util.*

class GatewayUdpChannel: UdpChannel() {
    private var pendingRequests = Collections.synchronizedMap(mutableMapOf<Pair<Long, InetSocketAddress>, Pair<ConnectedServer, Request>>())
    private var blockedRequests = Collections.synchronizedList(mutableListOf<Request>())
    var blockInput = false
    var syncInitiator: Request? = null
    var commits: MutableList<CommitDto> = mutableListOf()

    private val checkPendingRequests = object: TimerTask() {
        @Synchronized override fun run() {
            val now = ZonedDateTime.now().toEpochSecond()
            logger(LogLevel.DEBUG,"Pending requests: $pendingRequests")
            logger(LogLevel.DEBUG, "Available servers: $servers")
            pendingRequests.forEach {
                request -> if (now - request.key.first >= UdpConfig.holdRequestTimeout) {
                    logger(LogLevel.INFO,"Pending requests: $pendingRequests")
                    logger(LogLevel.INFO, "Available servers: $servers")
                    val req = request.value.second
                    try {
                        if (request.value.second.getSyncType().sync) {
                            blockInput = false
                            runBlockedRequests()
                        }
                        val sendAt = request.value.second.getHeader("sendAt")?.toString()?.toLong() ?: ZonedDateTime.now().toEpochSecond()
                        pendingRequests.remove(request.key)
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
                    pendingRequests.remove(request.key)
                }
            }
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
    }
    private var syncHelper: SyncHelper = SyncHelper()
    fun sendThrough(initiator: Request, updateWith: Request.() -> Unit) {
        val now = ZonedDateTime.now().toEpochSecond()
        initiator.apply(updateWith).apply {
            this setHeader Pair("id", now)
            this setHeader Pair("sync", syncHelper)
        }

        if (blockInput) {
            blockedRequests.add(initiator)
            return
        }

        val commandSyncType: CommandSyncType = initiator.getSyncType()

        if (commandSyncType.blocking) {
            if (commandSyncType.blockByArgument != null)
                syncHelper.removedInstances.add(initiator.data.inlineArguments[commandSyncType.blockByArgument].toString().toLong())
            if (commandSyncType.blockByDataValue != null)
                syncHelper.removedInstances.add(initiator.data.arguments[commandSyncType.blockByDataValue].toString().toLong())
        }

        val server = GatewayBalancer.getServer(this) ?: throw ResolveError(ResponseCode.CONNECTION_REFUSED)
        val serverAddress = server.address

        if (commandSyncType.sync) {
            this.blockInput = true
            this.syncInitiator = initiator
            initiator.apply {
                this setSyncHelper (this.getSyncHelper().apply {
                    commits = this@GatewayUdpChannel.commits
                    servers = this@GatewayUdpChannel.servers.filter { it.address != serverAddress }.map { it.address }.toMutableList()
                })
            }
            println("Commits [${commits.size}] $commits")
            this.commits = mutableListOf()
        }

        try {
            this.emit(serverAddress, initiator)
            pendingRequests[Pair(now, initiator.getFrom())] = (Pair(server, initiator))
        } catch (e: Exception) {
            logger(LogLevel.ERROR, "$e ${e.stackTraceToString()}")
            disconnectStrategy.onDisconnect(this, serverAddress, RequestDirection.FROM_SERVER)
            sendThrough(initiator) {}
        }
    }

    fun runBlockedRequests() {
        logger(LogLevel.DEBUG, "Run blocked request")
        blockedRequests.forEach {
            sendThrough(it) {}
        }
    }

    infix fun clearPending(request: Request): Boolean {
        val key = Pair(request.getHeader("id").toString().toLong(), request.getFrom())
        return if (pendingRequests.containsKey(key)) {
            pendingRequests.remove(key)
            true
        } else false
    }

    infix fun isPendingClient(request: Request): Boolean {
        pendingRequests.forEach {
            if (it.key.second == request.getFrom())
                return true
        }
        return false
    }

    override suspend fun run() {
        Timer().scheduleAtFixedRate(
            checkPendingRequests, UdpConfig.pendingRequestCheckTimeout, UdpConfig.pendingRequestCheckTimeout
        )

        super.run()
    }
}