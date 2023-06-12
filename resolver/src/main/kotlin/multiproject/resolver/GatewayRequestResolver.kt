package multiproject.resolver

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.gateway.ResolveError
import multiproject.lib.request.Request
import multiproject.lib.request.resolver.RequestResolver
import multiproject.lib.udp.gateway.GatewayUdpChannel
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import java.time.ZonedDateTime

class GatewayRequestResolver: RequestResolver() {
    private val gateway: GatewayUdpChannel by inject(GatewayUdpChannel::class.java, named("server"))
    private val logger: Logger by inject(Logger::class.java, named("logger"))
    override suspend fun resolveFirst(request: Request) {
        if (request directionIs RequestDirection.FROM_CLIENT)
            if (gateway isPendingClient request) throw ResolveError(ResponseCode.CONNECTION_REFUSED)
            else {
                gateway.sendThrough(request) {
                    path = PathDto("system", "_load")
                }
            }
        if (request directionIs RequestDirection.FROM_SERVER)
            gateway.addServer(ConnectedServer(0, ZonedDateTime.now().toEpochSecond(), request.getFrom()))
        else
            logger(LogLevel.WARN, "Unknown request have come $request")
    }

    override suspend fun resolve(request: Request) {
        if (request directionIs RequestDirection.FROM_CLIENT)
            gateway.clientRequestsChannel.send(Pair(request.getSender(), request))
        else if (request directionIs RequestDirection.FROM_SERVER)
            gateway.requestsChannel.send(Pair(request.getSender(), request))
        else
            logger(LogLevel.WARN, "Unknown request have come $request")
    }

    override fun resolveError(request: Request, e: ResolveError) {
        gateway.failedRequestsChannel.trySend(Pair(e, request))
    }

}