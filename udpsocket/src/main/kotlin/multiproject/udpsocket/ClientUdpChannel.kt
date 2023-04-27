package multiproject.udpsocket

import multiproject.udpsocket.dto.RequestDto
import multiproject.udpsocket.dto.ResponseCode
import multiproject.udpsocket.dto.ResponseDto
import multiproject.udpsocket.dto.Serializer
import java.net.InetSocketAddress
import java.net.PortUnreachableException
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.util.*

class ClientUdpChannel(val onConnectionRefused: OnConnectionRefused, val onConnectionRestored: OnConnectionRestored) {
    private val serverAddress: SocketAddress = InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort)
    private val channel: DatagramChannel = DatagramChannel.open()

    init {
        channel.bind(null)
        channel.connect(serverAddress)
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
        return try {
            Serializer.deserializeResponse(this.getMessage())
        } catch (e: PortUnreachableException) {
            var attemptNum = 0
            val reconnectTask: TimerTask = object: TimerTask() {
                override fun run() {
                    attemptNum++
                    println("Reconnect attempt #$attemptNum")
                    channel.send(ByteBuffer.wrap(dataString.toByteArray()), serverAddress)
                    try {
                        onConnectionRestored.process(Serializer.deserializeResponse(getMessage()))
                        println("Connection restored!")
                        this.cancel()
                    } catch (e: PortUnreachableException) {
                        //
                    }
                }
            }
            Timer().schedule(
                reconnectTask, UdpConfig.timeout, UdpConfig.reconnectTimeout
            )

            this.onConnectionRefused.process(data)
            ResponseDto(code = ResponseCode.CONNECTION_REFUSED, "Connection refused! Try to reconnect...")
        }
    }
}