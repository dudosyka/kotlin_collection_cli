package multiproject.lib.udp.server

import multiproject.lib.dto.Serializer
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class ServerUdpChannel() {
    private var receiveCallback: OnReceive = OnReceive {
        _, _, _ ->
    }

    private var firstConnectCallback: OnConnect = OnConnect {
        _, _ ->
    }

    private val channel = DatagramChannel.open()

    private var addr: InetSocketAddress = InetSocketAddress(3000)

    private var connections: MutableList<SocketAddress> = mutableListOf()

    private var continueRun = true

    fun onReceive(callback: OnReceive) {
        receiveCallback = callback
    }

    fun onFirstConnect(callback: OnConnect) {
        firstConnectCallback = callback
    }

    fun serverAddress(serverAddress: String, serverPort: Int) {
        addr = InetSocketAddress(serverAddress, serverPort)
    }

    fun run() {
        channel.bind(addr)

        println("Server successfully start up!")

        this.receive()
    }

    private fun processBuffer(buffer: ByteBuffer): String {
        buffer.flip()
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return String(bytes)
    }

    private fun receive() {
        while (this.continueRun) {
            val buffer = ByteBuffer.allocate(65535)
            val address: SocketAddress = channel.receive(buffer)

            if (!connections.contains(address)) {
                this.onNewConnection(address)
            } else {
                this.onMessage(address, this.processBuffer(buffer))
            }
        }
    }

    private fun onMessage(address: SocketAddress, data: String) {
        this.receiveCallback.process(channel, address, Serializer.deserializeRequest(data))
    }

    private fun onNewConnection(address: SocketAddress) {
        connections.add(address)
        this.firstConnectCallback.process(channel, address)
    }

    fun disconnect(address: SocketAddress) {
        connections.remove(address)
    }
}