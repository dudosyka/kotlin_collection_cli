package multiproject.lib.udp.client

import multiproject.lib.dto.RequestDto
import multiproject.lib.dto.ResponseDto
import multiproject.lib.dto.Serializer
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.UdpConfig
import java.net.PortUnreachableException
import java.nio.ByteBuffer
import java.util.*

class ClientUdpChannel: UdpChannel() {
    private var ping: Boolean = true
    private var connectionLost: Boolean = false
    private var attemptNum: Int = 0
    private var pingReconnect: TimerTask? = null
    private fun pingServer() {
        pingReconnect = object: TimerTask() {
            override fun run() {
                if (!ping)
                    return

                try {
                    channel.send(ByteBuffer.wrap(Serializer.serializeRequest(RequestDto("")).toByteArray()), servers.first())
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
    fun sendRequest(data: RequestDto): ResponseDto {
        return super.send(this.servers.first(), data)
    }
    override fun run() {
        channel.connect(this.servers.first())
        channel.configureBlocking(false)
        this.pingServer()
    }

    override fun stop() {
        this.pingReconnect!!.cancel()
        super.stop()
    }
}