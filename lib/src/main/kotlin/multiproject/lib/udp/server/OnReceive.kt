package multiproject.lib.udp.server

import multiproject.lib.dto.RequestDto
import java.net.SocketAddress
import java.nio.channels.DatagramChannel

fun interface OnReceive {
    fun process(channel: DatagramChannel, address: SocketAddress, data: RequestDto)
}