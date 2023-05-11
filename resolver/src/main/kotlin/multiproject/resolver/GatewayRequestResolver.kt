package multiproject.resolver

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.request.RequestDirectionInterpreter
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
        when (RequestDirectionInterpreter.interpret(request.requestDirection)) {
            RequestDirection.FROM_CLIENT -> {
                gateway.sendThrough(request) {
                    pathDto = PathDto("system","_load")
                }
            }
            RequestDirection.FROM_SERVER -> {
                gateway.addServer(ConnectedServer(0, request.from))
            }
            else -> {
                throw BadRequestException("Unknown request")
            }
        }
    }

    override fun resolve(request: Request) {
        when (RequestDirectionInterpreter.interpret(request.requestDirection)) {
            RequestDirection.FROM_CLIENT -> {
                gateway.sendThrough(request) {}
            }
            RequestDirection.FROM_SERVER -> {
                gateway.send(request.from, request.dto)
            }
            else -> {
                throw BadRequestException("Unknown request")
            }
        }
    }

    override fun resolveError(request: Request, e: ResolveError) {
        println(e.message)
        if (e.code == ResponseCode.CONNECTION_REFUSED)
            gateway.emit(request.from, ResponseDto( e.code, result = "Server unavailable. Connection refused." ))
    }

}