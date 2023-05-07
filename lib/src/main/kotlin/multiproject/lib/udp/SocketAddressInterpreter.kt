package multiproject.lib.udp

import java.net.InetSocketAddress
import java.net.SocketAddress

object SocketAddressInterpreter {
    fun interpret(address: SocketAddress): InetSocketAddress {
        return SocketAddressInterpreter.interpret(address.toString())
    }

    fun interpret(address: String): InetSocketAddress {
        val pair = address.toString().split("/")[1].split(":")
        return InetSocketAddress(pair[0], pair[1].split(";")[0].toInt())
    }
}