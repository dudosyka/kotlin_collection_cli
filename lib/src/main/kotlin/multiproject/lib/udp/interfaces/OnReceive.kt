package multiproject.lib.udp.interfaces

import multiproject.lib.dto.request.RequestDto
import java.net.SocketAddress

fun interface OnReceive {
    fun process(address: SocketAddress, data: RequestDto)
}