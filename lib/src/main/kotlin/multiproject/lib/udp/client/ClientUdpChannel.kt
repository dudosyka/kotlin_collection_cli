package multiproject.lib.udp.client

import multiproject.lib.dto.request.RequestDto
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.dto.Serializer
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.request.RequestDirectionInterpreter
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.UdpConfig
import java.net.PortUnreachableException
import java.nio.ByteBuffer
import java.util.*

class ClientUdpChannel: UdpChannel() {
    private var attemptNum: Int = 0
    private var pingReconnect: TimerTask? = null
    lateinit var defaultController: String
    var authorized: Boolean = false
    private var token: String = ""
    private fun pingServer() {
        pingReconnect = object: TimerTask() {
            override fun run() {
                if (attemptNum > 0)
                    return

                try {
                    channel.send(
                        ByteBuffer.wrap(
                            Serializer.serializeRequest(
                                RequestDto(
                                    PathDto("", ""),
                                    headers = mutableMapOf(
                                        "requestDirection" to RequestDirectionInterpreter.interpret(RequestDirection.FROM_CLIENT)
                                    )
                                )
                            ).toByteArray()
                        ),
                        servers.first().address
                    )
                } catch (e: PortUnreachableException) {
                    wasDisconnected = true
                    if (attemptNum <= 0)
                        onConnectionRefusedCallback.process()
                    attemptNum++
                    println("Reconnect attempt #$attemptNum")
                    sendRequest(RequestDto(PathDto("system", "_load")))
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
        data.headers["requestDirection"] = RequestDirectionInterpreter.interpret(RequestDirection.FROM_CLIENT)
        if (authorized)
            data.headers["token"] = token
        val response = super.send(this.servers.first().address, data)
        return response
    }
    fun auth(token: String) {
        this.authorized = true
        this.token = token
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