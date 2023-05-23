package multiproject.lib.dto

import java.net.InetSocketAddress

data class ConnectedServer(
    var pendingRequest: Int,
    var lastRequest: Long,
    val address: InetSocketAddress,
    var temporaryUnavailable: Pair<Long, Boolean> = Pair(0, false), //if true, first item contains time when server become unavailable
)