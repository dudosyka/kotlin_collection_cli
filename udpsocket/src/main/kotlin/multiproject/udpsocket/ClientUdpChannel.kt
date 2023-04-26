package multiproject.udpsocket

import multiproject.udpsocket.dto.RequestDto
import multiproject.udpsocket.dto.ResponseCode
import multiproject.udpsocket.dto.ResponseDto
import multiproject.udpsocket.dto.Serializer
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class ClientUdpChannel {
    private val serverAddress: SocketAddress = InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort)
    private val channel: DatagramChannel = DatagramChannel.open()

    init {
        channel.bind(null)
        channel.configureBlocking(false)
    }

    private fun getMessage(): String {
        var data: String? = null
        while (data.isNullOrEmpty()) {
            val buffer: ByteBuffer = ByteBuffer.allocate(65535)
            channel.receive(buffer)
            buffer.flip()
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            data = String(bytes)
            //TODO handle connection refused.
        }
        return data
    }

    fun sendRequest(data: RequestDto): ResponseDto {
        val dataString = Serializer.serializeRequest(data)
        channel.send(ByteBuffer.wrap(dataString.toByteArray()), serverAddress)
        return Serializer.deserializeResponse(this.getMessage())
    }
}