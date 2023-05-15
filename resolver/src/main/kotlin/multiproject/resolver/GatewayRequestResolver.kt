package multiproject.resolver

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.exceptions.BadRequestException
import multiproject.lib.request.Request
import multiproject.lib.request.resolver.RequestResolver
import multiproject.lib.request.resolver.ResolveError
import multiproject.lib.udp.gateway.GatewayUdpChannel
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class GatewayRequestResolver: RequestResolver() {
    private val gateway: GatewayUdpChannel by inject(GatewayUdpChannel::class.java, named("server"))
    override fun resolveFirst(request: Request) {
        if (request directionIs RequestDirection.FROM_CLIENT)
            if (gateway isPendingClient request) throw ResolveError(ResponseCode.CONNECTION_REFUSED)
            else {
                gateway.sendThrough(request) {
                    path = PathDto("system", "_load")
                }
            }
        else if (request directionIs RequestDirection.FROM_SERVER)
            gateway.addServer(ConnectedServer(0, request.getFrom()))
        else
            throw BadRequestException("Unknown request")
    }

    override fun resolve(request: Request) {
        if (request directionIs RequestDirection.FROM_CLIENT)
            gateway.sendThrough(request) {}
        else if (request directionIs RequestDirection.FROM_SERVER) {
            gateway clearPending request
            gateway.servers.find { it.address == request.getSender() }?.apply {
                temporaryUnavailable = Pair(0, false)
                pendingRequest--
            }
            val from = request.getFrom()
            request.removeSystemHeaders()
            gateway.emit(from, request)
        }
        else
            throw BadRequestException("Unknown request")
    }

    override fun resolveError(request: Request, e: ResolveError) {
        if (e.code == ResponseCode.CONNECTION_REFUSED) {
            request.apply {
                response = ResponseDto(e.code, result = "Server unavailable. Connection refused.")
            }
            gateway.emit(request.getFrom(), request)
        }
    }

}