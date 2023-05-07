package multiproject.lib.udp.client

import multiproject.lib.dto.RequestDto
import multiproject.lib.dto.ResponseCode
import multiproject.lib.dto.ResponseDto
import multiproject.lib.dto.Serializer
import multiproject.lib.udp.UdpConfig
import java.net.InetSocketAddress
import java.net.PortUnreachableException
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.util.*

class ClientUdpChannel() {
    private var onConnectionRefusedCallback: OnConnectionRefused = OnConnectionRefused {
        _ ->
    }
    private var onConnectionRestoredCallback: OnConnectionRestored = OnConnectionRestored {
        _ ->
    }
    private var serverAddress: SocketAddress = InetSocketAddress(3000)
    private val channel: DatagramChannel = DatagramChannel.open()
    private var ping: Boolean = true
    private var connectionLost: Boolean = false
    private var attemptNum: Int = 0
    private var pingReconnect: TimerTask? = null

    fun onConnectionRefused(callback: OnConnectionRefused) {
        onConnectionRefusedCallback = callback
    }

    fun onConnectionRestored(callback: OnConnectionRestored) {
        onConnectionRestoredCallback = callback
    }

    fun setServer(serverAddress: String, serverPort: Int) {
        this.serverAddress = InetSocketAddress(serverAddress, serverPort)
    }

    fun connect() {
        channel.connect(this.serverAddress)
        channel.configureBlocking(false)
        pingServer()
    }

    private fun pingServer() {
        pingReconnect = object: TimerTask() {
            override fun run() {
                if (!ping)
                    return

                try {
                    channel.send(ByteBuffer.wrap(Serializer.serializeRequest(RequestDto("")).toByteArray()), serverAddress)
                    if (connectionLost)
                        onConnectionRestoredCallback.process(Serializer.deserializeResponse(getMessage()))
                    connectionLost = false
                    attemptNum = 0
                } catch (e: PortUnreachableException) {
                    connectionLost = true
                    if (attemptNum <= 0)
                        onConnectionRefusedCallback.process(RequestDto("ping"))
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
                        onConnectionRestoredCallback.process(Serializer.deserializeResponse(getMessage()))
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

            this.onConnectionRefusedCallback.process(data)
            ResponseDto(code = ResponseCode.CONNECTION_REFUSED, "Connection refused! Try to reconnect...")
        }
    }

    fun stop() {
        this.pingReconnect!!.cancel()
        channel.close()
    }
}