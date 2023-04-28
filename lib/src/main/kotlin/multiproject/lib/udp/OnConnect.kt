package multiproject.lib.udp

import java.net.SocketAddress
import java.nio.channels.DatagramChannel

fun interface OnConnect {
    fun process(channel: DatagramChannel, address: SocketAddress)
}