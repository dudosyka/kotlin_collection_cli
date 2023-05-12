package multiproject.lib.udp.gateway

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.exceptions.NoAvailableServers
import multiproject.lib.request.Request
import multiproject.lib.request.resolver.ResolveError
import multiproject.lib.sd.GatewayBalancer
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.UdpConfig
import multiproject.lib.utils.LogLevel
import java.net.InetSocketAddress
import java.time.ZonedDateTime
import java.util.*

class GatewayUdpChannel: UdpChannel() {
    private var pendingRequests = mutableMapOf<Pair<Long, InetSocketAddress>, Pair<ConnectedServer, Request>>()
    fun sendThrough(initiator: Request, updateWith: Request.() -> Unit) {
        val now = ZonedDateTime.now().toEpochSecond()
        initiator.apply(updateWith).apply {
            this setHeader Pair("id", now)
        }

        val server = GatewayBalancer.getServer(this) ?: throw NoAvailableServers()
        val serverAddress = server.address

        try {
            this.emit(serverAddress, initiator)
            pendingRequests[Pair(now, initiator.getFrom())] = (Pair(server, initiator))
        } catch (e: Exception) {
            logger(LogLevel.ERROR, e.message ?: "Fatal error!")
            disconnectStrategy.onDisconnect(this, serverAddress, RequestDirection.FROM_SERVER)
            sendThrough(initiator) {}
        }
    }

    infix fun clearPending(request: Request) {
        pendingRequests.remove(Pair(request.getHeader("id").toString().toLong(), request.getFrom()))
        logger(LogLevel.INFO, "Pending requests: $pendingRequests")
    }

    infix fun isPendingClient(request: Request): Boolean {
        pendingRequests.forEach {
            if (it.key.second == request.getFrom())
                return true
        }
        return false
    }

    override fun run() {
        val checkPendingRequests = object: TimerTask() {
            override fun run() {
                val now = ZonedDateTime.now().toEpochSecond()
                logger(LogLevel.DEBUG,"Pending requests: $pendingRequests")
                logger(LogLevel.DEBUG, "Available servers: $servers")
                pendingRequests = pendingRequests.filter {
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
                            sendThrough(req) {}
                        } catch (e: ResolveError) {
                            if (e.code == ResponseCode.CONNECTION_REFUSED) {
                                req.apply {
                                    response = ResponseDto(e.code, result = "Server unavailable. Connection refused.")
                                }
                                emit(req.getFrom(), req)
                            }
                        }
                        false
                    } else {
                        true
                    }
                }.toMutableMap()
                servers = servers.filter {
                    server -> if (server.temporaryUnavailable.second) {
                        now - server.temporaryUnavailable.first < UdpConfig.removeAfterUnavailableTimeout
                    } else true
                }.toMutableList()
            }
        }

        Timer().scheduleAtFixedRate(
            checkPendingRequests, UdpConfig.pendingRequestCheckTimeout, UdpConfig.pendingRequestCheckTimeout
        )

        super.run()
    }
}