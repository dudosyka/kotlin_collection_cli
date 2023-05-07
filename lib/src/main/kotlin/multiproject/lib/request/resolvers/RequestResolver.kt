package multiproject.lib.request.resolvers

import multiproject.lib.dto.request.RequestDto
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.udp.gateway.GatewayUdpChannel
import java.net.InetSocketAddress
import java.net.SocketAddress

abstract class RequestResolver(val gateway: GatewayUdpChannel, val from: InetSocketAddress) {
    var result: ResponseDto? = null;
    abstract fun resolve(requestDto: RequestDto?);
}