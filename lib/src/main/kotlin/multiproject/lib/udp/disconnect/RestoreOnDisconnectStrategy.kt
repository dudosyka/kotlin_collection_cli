package multiproject.lib.udp.disconnect

import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.request.Request
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.UdpConfig
import java.net.InetSocketAddress
import java.util.*

class RestoreOnDisconnectStrategy: DisconnectStrategy() {
    override fun onDisconnect(channel: UdpChannel, address: InetSocketAddress, requestDirection: RequestDirection): ResponseDto {

        val reconnectTask: TimerTask = object: TimerTask() {
            override fun run() {
                println("On disconnect triggered")
                attemptNum++
                if (channel.wasDisconnected)
                    channel.send(
                        address,
                        Request(PathDto("system", "_load")).apply {
                            this setDirection requestDirection
                            this setFrom channel.getChannelAddress()
                        }
                    )
            }
        }
        Timer().schedule(
            reconnectTask, UdpConfig.timeout
        )

        channel.onDisconnectAttempt.process(attemptNum + 1)

        return ResponseDto(code = ResponseCode.CONNECTION_REFUSED, "Server unavailable")
    }
}