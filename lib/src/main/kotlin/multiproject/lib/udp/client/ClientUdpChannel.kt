package multiproject.lib.udp.client

import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.request.Request
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.UdpConfig
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Serializer
import java.net.InetSocketAddress
import java.net.PortUnreachableException
import java.nio.ByteBuffer
import java.time.ZonedDateTime
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
                                Request(
                                    PathDto("", ""),
                                ).apply {
                                    this setFrom channel.localAddress
                                    this setDirection RequestDirection.FROM_CLIENT
                                }
                            ).toByteArray()
                        ),
                        servers.first().address
                    )
                } catch (e: PortUnreachableException) {
                    wasDisconnected = true
                    if (attemptNum <= 0)
                        onConnectionRefusedCallback.process()
                    attemptNum++
                    sendRequest(Request(PathDto("system", "_load")))
                } catch (e: Exception) {
                    logger(LogLevel.ERROR, "Error in ping timer:", e)
                }
            }
        }
        Timer().scheduleAtFixedRate(
            pingReconnect, UdpConfig.timeout, UdpConfig.reconnectTimeout
        )
    }
    fun sendRequest(request: Request): ResponseDto {
        request setDirection RequestDirection.FROM_CLIENT
        request setHeader Pair("sendAt", ZonedDateTime.now().toEpochSecond())
        if (authorized)
            request setHeader Pair("token", token)
        request setFrom channel.localAddress
        val result = send(this.servers.first().address, request)
        return result.response
    }
    fun auth(token: String) {
        this.authorized = true
        this.token = token
    }
    override suspend fun run() {
        channel.connect(this.servers.first().address)
        channel.configureBlocking(false)
        this.pingServer()
    }

    override fun send(address: InetSocketAddress, data: Request): Request  {
        logger(LogLevel.INFO, "Send to $address with data: $data")
        val dataString = Serializer.serializeRequest(data)
        val response = try {
            channel.send(ByteBuffer.wrap(dataString.toByteArray()), address)

            val msg = this.getMessage()
            val returnedFromServer = Serializer.deserializeRequest(msg)

            if (returnedFromServer checkCode ResponseCode.CONNECTION_REFUSED)
                throw PortUnreachableException()

            if (wasDisconnected) {
                disconnectStrategy.attemptNum = 0
                wasDisconnected = false
                onConnectionRestoredCallback.process(returnedFromServer.response)
            }

            returnedFromServer

        } catch (e: PortUnreachableException) {
            if (this.disconnectStrategy.attemptNum >= 0)
                wasDisconnected = true

            if (this.disconnectStrategy.attemptNum == 0)
                onConnectionRefusedCallback.process()

            val response = disconnectStrategy.onDisconnect(this, address, RequestDirection.FROM_CLIENT)
            data.response = response
            data
        }
        logger(LogLevel.INFO, "Returned response from $address with data $response")
        return response
    }

    override fun stop() {
        this.pingReconnect!!.cancel()
        super.stop()
    }
}