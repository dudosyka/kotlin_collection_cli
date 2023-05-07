package multiproject.lib.udp.interfaces

import multiproject.lib.dto.request.RequestDto
import java.net.SocketAddress
import java.nio.channels.DatagramChannel

fun interface OnConnect {
    fun process(channel: DatagramChannel, address: SocketAddress, data: RequestDto)
}