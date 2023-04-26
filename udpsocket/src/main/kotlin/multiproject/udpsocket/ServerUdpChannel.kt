package multiproject.udpsocket

import multiproject.udpsocket.dto.ResponseDto
import multiproject.udpsocket.dto.Serializer
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class ServerUdpChannel(
    val onReceive: OnReceive,
    val onFirstConnect: OnConnect
) {
    var connections: MutableList<SocketAddress> = mutableListOf()

    var continueRun = true

    fun run() {
        val channel = DatagramChannel.open()
        val addr = InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort)

        channel.bind(addr)

        println("Server successfully start up!")

        while (this.continueRun) {
            val buffer = ByteBuffer.allocate(65535)
            val address: SocketAddress = channel.receive(buffer)
            buffer.flip()
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            val data = String(bytes)
            if (!this.connections.contains(address)) {
                this.connections.add(address)
                this.firstConnect(channel, address)
            } else {
                this.receive(channel, address, data)
            }
        }
    }

    fun stop() {
        this.continueRun = false
    }

    private fun receive(channel: DatagramChannel, address: SocketAddress, data: String) {
        this.onReceive.process(channel, address, Serializer.deserializeRequest(data))
    }

    companion object {
        fun send(channel: DatagramChannel, address: SocketAddress, responseDto: ResponseDto) {
            channel.send(ByteBuffer.wrap(Serializer.serializeResponse(responseDto).toByteArray()), address)
        }
    }

    private fun firstConnect(channel: DatagramChannel, address: SocketAddress) {
        this.onFirstConnect.process(channel, address)
    }
}