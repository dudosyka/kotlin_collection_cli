package multiproject.lib.udp.server

import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.request.Request
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.server.router.Router
import java.net.InetSocketAddress
import java.net.SocketAddress

class ServerUdpChannel: UdpChannel() {
    lateinit var router: Router

    fun applyRouter(init: Router.() -> Unit) {
        this.router = Router().apply(init)
    }

    fun selfExecute(request: Request) {
        this.router.run(request.apply { this setFrom getChannelAddress()})
    }

    override fun onNewConnection(address: SocketAddress, data: String) {
        this.onMessage(address, data)
    }

    override fun run() {
        this.emit(
            InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort),
            Request(
                PathDto(route = "_bind", controller = "system")
            ).apply {
                this setDirection RequestDirection.FROM_SERVER
                this setFrom channel.localAddress
            },
        )
        super.run()
    }
}