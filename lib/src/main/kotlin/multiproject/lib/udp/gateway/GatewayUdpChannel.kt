package multiproject.lib.udp.gateway

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.command.CommitDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.exceptions.NoAvailableServers
import multiproject.lib.request.Request
import multiproject.lib.request.resolver.ResolveError
import multiproject.lib.sd.GatewayBalancer
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.server.router.CommandSyncType
import multiproject.lib.utils.LogLevel
import java.net.InetSocketAddress
import java.time.ZonedDateTime
import java.util.*

class GatewayUdpChannel: UdpChannel() {
    @Volatile private var pendingRequests = Collections.synchronizedMap(mutableMapOf<Pair<Long, InetSocketAddress>, Pair<ConnectedServer, Request>>())
    private var blockedRequests = Collections.synchronizedList(mutableListOf<Request>())
    var blockInput = false
    var syncInitiator: Request? = null
    var commits: MutableList<CommitDto> = mutableListOf()


    private val checkPendingRequests2 = object: TimerTask() {
        override fun run() {

        }
    }

    private val checkPendingRequests = object: TimerTask() {
        @Synchronized override fun run() {
            val now = ZonedDateTime.now().toEpochSecond()
            logger(LogLevel.DEBUG,"Pending requests: $pendingRequests")
            logger(LogLevel.DEBUG, "Available servers: $servers")
            pendingRequests.forEach {
                request -> if (now - request.key.first >= UdpConfig.unavailableTimeout) {
                    val req = request.value.second
                    servers.replaceAll {
                        if (it == request.value.first) {
                            it.apply { temporaryUnavailable = Pair(now, true) }
                            it
                        } else
                            it
                    }
                    try {
                        if (request.value.second.getSyncType().sync) {
                            blockInput = false
                            runBlockedRequests()
                        }
                        sendThrough(req) {}
                    } catch (e: ResolveError) {
                        if (e.code == ResponseCode.CONNECTION_REFUSED) {
                            req.apply {
                                response = ResponseDto(e.code, result = "Server unavailable. Connection refused.")
                            }
                            val from = req.getFrom()
                            req.removeSystemHeaders()
                            emit(from, req)
                        }
                    }
                    pendingRequests.remove(request.key)
                }
            }
            servers = servers.filter {
                    server -> if (server.temporaryUnavailable.second) {
                now - server.temporaryUnavailable.first < UdpConfig.removeAfterUnavailableTimeout
            } else true
            }.toMutableList()
        }
    }
    private var syncHelper: SyncHelper = SyncHelper()
    @Synchronized fun sendThrough(initiator: Request, updateWith: Request.() -> Unit) {
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

        val server = GatewayBalancer.getServer(this) ?: throw NoAvailableServers()
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
            commits = mutableListOf()
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
        blockedRequests.clear()
    }

    infix fun clearPending(request: Request) {
        pendingRequests.remove(Pair(request.getHeader("id").toString().toLong(), request.getFrom()))
    }

    infix fun isPendingClient(request: Request): Boolean {
        pendingRequests.forEach {
            if (it.key.second == request.getFrom())
                return true
        }
        return false
    }

    override fun run() {
        Timer().scheduleAtFixedRate(
            checkPendingRequests, UdpConfig.pendingRequestCheckTimeout, UdpConfig.pendingRequestCheckTimeout
        )
        Timer().schedule(checkPendingRequests2, UdpConfig.pendingRequestCheckTimeout)

        super.run()
    }
}