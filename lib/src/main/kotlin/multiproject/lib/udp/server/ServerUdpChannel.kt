package multiproject.lib.udp.server

import multiproject.lib.dto.request.RequestDto
import multiproject.lib.udp.SocketAddressInterpreter
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.UdpConfig
import java.net.InetSocketAddress

class ServerUdpChannel: UdpChannel() {
    override fun run() {
        this.emit(
            InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort),
            RequestDto(
                "_bind",
                headers = mutableMapOf(
                    "requestDirection" to 2
                )
            )
        );
        super.run()
    }
}