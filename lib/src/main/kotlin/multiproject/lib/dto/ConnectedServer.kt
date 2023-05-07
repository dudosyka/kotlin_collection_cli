package multiproject.lib.dto

import java.net.InetSocketAddress
import java.net.SocketAddress

data class ConnectedServer(
    var pendingRequest: Int,
    val address: InetSocketAddress
)