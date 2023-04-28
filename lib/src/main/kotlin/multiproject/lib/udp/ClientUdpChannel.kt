package multiproject.lib.udp

import multiproject.lib.dto.RequestDto
import multiproject.lib.dto.ResponseCode
import multiproject.lib.dto.ResponseDto
import multiproject.lib.dto.Serializer
import java.net.InetSocketAddress
import java.net.PortUnreachableException
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.util.*

class ClientUdpChannel(val onConnectionRefused: OnConnectionRefused, val onConnectionRestored: OnConnectionRestored) {
    private val serverAddress: SocketAddress = InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort)
    private val channel: DatagramChannel = DatagramChannel.open()
    private var ping: Boolean = true
    private var connectionLost: Boolean = false
    private var attemptNum: Int = 0
    private var pingReconnect: TimerTask? = null

    init {
        channel.bind(null)
        channel.connect(serverAddress)
        channel.configureBlocking(false)
        this.pingServer()
    }

    private fun pingServer() {
        pingReconnect = object: TimerTask() {
            override fun run() {
                if (!ping)
                    return

                try {
                    channel.send(ByteBuffer.wrap(Serializer.serializeRequest(RequestDto("")).toByteArray()), serverAddress)
                    if (connectionLost)
                        onConnectionRestored.process(Serializer.deserializeResponse(getMessage()))
                    connectionLost = false
                    attemptNum = 0
                } catch (e: PortUnreachableException) {
                    connectionLost = true
                    if (attemptNum <= 0)
                        onConnectionRefused.process(RequestDto("ping"))
                    attemptNum++
                    println("Reconnect attempt #$attemptNum")
                } catch (e: Exception) {
                    println(e)
                }
            }
        }
        Timer().scheduleAtFixedRate(
            pingReconnect, UdpConfig.timeout, UdpConfig.reconnectTimeout
        )
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
        return try {
            channel.send(ByteBuffer.wrap(dataString.toByteArray()), serverAddress)
            Serializer.deserializeResponse(this.getMessage())
        } catch (e: PortUnreachableException) {
            attemptNum = 0
            ping = false
            val reconnectTask: TimerTask = object: TimerTask() {
                override fun run() {
                    attemptNum++
                    println("Reconnect attempt #$attemptNum")
                    channel.send(ByteBuffer.wrap(dataString.toByteArray()), serverAddress)
                    try {
                        onConnectionRestored.process(Serializer.deserializeResponse(getMessage()))
                        println("Connection restored!")
                        this.cancel()
                        ping = true
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

    fun stop() {
        this.pingReconnect!!.cancel()
        channel.close()
    }
}