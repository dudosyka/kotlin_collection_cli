package multiproject.lib.request.resolvers

import multiproject.lib.dto.request.RequestDto
import multiproject.lib.udp.gateway.GatewayUdpChannel
import java.net.InetSocketAddress
import java.net.SocketAddress

class FirstRequestResolver(gateway: GatewayUdpChannel, from: InetSocketAddress) : RequestResolver(gateway, from) {
    override fun resolve(requestDto: RequestDto?) {

    }
}