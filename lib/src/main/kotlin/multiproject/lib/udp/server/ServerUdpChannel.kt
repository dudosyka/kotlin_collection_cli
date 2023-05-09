package multiproject.lib.udp.server

import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDto
import multiproject.lib.request.Request
import multiproject.lib.udp.SocketAddressInterpreter
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

    private fun getAddress(): InetSocketAddress {
        return SocketAddressInterpreter.interpret(this.channel.localAddress)
    }

    fun selfExecute(request: RequestDto, from: InetSocketAddress? = null) {
        this.router.run(Request(request, from ?: this.getAddress()))
    }

    override fun onNewConnection(address: SocketAddress, data: String) {
        this.onMessage(address, data)
    }

    override fun run() {
        this.emit(
            InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort),
            RequestDto(
                PathDto(
                    route = "_bind",
                    controller = "system"
                ),
                headers = mutableMapOf(
                    "requestDirection" to 2
                )
            )
        )
        super.run()
    }
}