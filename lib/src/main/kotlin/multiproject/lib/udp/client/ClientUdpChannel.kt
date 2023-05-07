package multiproject.lib.udp.client

import multiproject.lib.dto.request.RequestDto
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.dto.Serializer
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.request.RequestDirectionInterpreter
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
                    channel.send(
                        ByteBuffer.wrap(
                            Serializer.serializeRequest(
                                RequestDto(
                                    "",
                                    headers = mutableMapOf(
                                        "requestDirection" to RequestDirectionInterpreter.interpret(RequestDirection.FROM_CLIENT)
                                    )
                                )
                            ).toByteArray()
                        ),
                        servers.first().address
                    )
                    if (connectionLost)
                        onConnectionRestoredCallback.process(Serializer.deserializeResponse(getMessage()))
                    connectionLost = false
                    attemptNum = 0
                } catch (e: PortUnreachableException) {
                    connectionLost = true
                    if (attemptNum <= 0)
                        onConnectionRefusedCallback.process(RequestDto(
                            "ping",
                            headers = mutableMapOf(
                                "requestDirection" to RequestDirectionInterpreter.interpret(RequestDirection.FROM_CLIENT)
                            )
                        ))
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
        data.headers["requestDirection"] = RequestDirectionInterpreter.interpret(RequestDirection.FROM_CLIENT);
        val response = super.send(this.servers.first().address, data)
        println("Response resolved: $response");
        return response;
    }
    override fun run() {
        channel.connect(this.servers.first().address)
        channel.configureBlocking(false)
        this.pingServer()
    }

    override fun stop() {
        this.pingReconnect!!.cancel()
        super.stop()
    }
}