package multiproject.lib.request.resolvers

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.request.RequestDto
import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.gateway.GatewayUdpChannel
import java.net.InetSocketAddress
import java.net.SocketAddress

class ServerFirstRequestResolver(gateway: GatewayUdpChannel, from: InetSocketAddress) : RequestResolver(gateway, from) {
    override fun resolve(requestDto: RequestDto?) {
        gateway.addServer(ConnectedServer(0, from))
    }
}