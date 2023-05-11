package multiproject.lib.udp.interfaces

import multiproject.lib.request.Request
import java.net.SocketAddress

fun interface OnReceive {
    fun process(address: SocketAddress, data: Request)
}