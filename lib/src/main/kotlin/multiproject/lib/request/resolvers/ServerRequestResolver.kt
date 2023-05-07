package multiproject.lib.request.resolvers

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.request.RequestDto
import multiproject.lib.udp.SocketAddressInterpreter
import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.gateway.GatewayUdpChannel
import java.net.InetSocketAddress
import java.net.SocketAddress

class ServerRequestResolver(gateway: GatewayUdpChannel, from: InetSocketAddress) : RequestResolver(gateway, from) {
    override fun resolve(requestDto: RequestDto?) {
        val addressString = requestDto!!.headers["client"].toString()
        val result = gateway.send(SocketAddressInterpreter.interpret(addressString), requestDto)
        print("Result returned! ${result}");
    }
}