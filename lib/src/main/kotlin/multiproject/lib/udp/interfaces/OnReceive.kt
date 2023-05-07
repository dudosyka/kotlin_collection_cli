package multiproject.lib.udp.interfaces

import multiproject.lib.dto.request.RequestDto
import java.net.SocketAddress
import java.nio.channels.DatagramChannel

fun interface OnReceive {
    fun process(channel: DatagramChannel, address: SocketAddress, data: RequestDto)
}