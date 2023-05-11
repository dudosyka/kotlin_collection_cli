package multiproject.lib.udp

import multiproject.lib.exceptions.InvalidSocketAddress
import java.net.InetSocketAddress
import java.net.SocketAddress

object SocketAddressInterpreter {
    fun interpret(address: SocketAddress): InetSocketAddress {
        return interpret(address.toString())
    }

    fun interpret(address: String): InetSocketAddress {
        try {
            val pair = address.split("/")[1].split(":")
            return InetSocketAddress(pair[0], pair[1].split(";")[0].toInt())
        } catch (e: Exception) {
            throw InvalidSocketAddress()
        }
    }

    fun interpret(address: InetSocketAddress): String {
        return address.toString()
    }
}