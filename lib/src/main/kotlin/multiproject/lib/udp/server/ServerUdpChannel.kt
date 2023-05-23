package multiproject.lib.udp.server

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.Response
import multiproject.lib.request.Request
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.server.router.Router
import java.net.InetSocketAddress
import java.net.SocketAddress

class ServerUdpChannel: UdpChannel() {
    lateinit var router: Router
    var requestsChannel: Channel<Pair<SocketAddress, Request>> = Channel(capacity = Channel.BUFFERED)
    var responseChannel: Channel<ResponseChannelItem> = Channel(capacity = Channel.BUFFERED)

    data class ResponseChannelItem (
        val from: SocketAddress,
        val request: Request,
        val response: Deferred<Response>
    )

    fun applyRouter(init: Router.() -> Unit) {
        this.router = Router(logger).apply(init)
    }

    override suspend fun onNewConnection(address: SocketAddress, data: String) {
        this.onMessage(address, data)
    }

    fun bindToResolver() {
        emit(
                InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort),
                Request(
                        PathDto(route = "_bind", controller = "system")
                ).apply {
                    this setDirection RequestDirection.FROM_SERVER
                    this setFrom channel.localAddress
                },
        )
    }

    override suspend fun run() {
        coroutineScope {
            launch {
                super.run()
            }
        }
    }
}