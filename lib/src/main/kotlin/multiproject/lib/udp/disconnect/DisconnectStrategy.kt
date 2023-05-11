package multiproject.lib.udp.disconnect

import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.udp.UdpChannel
import java.net.InetSocketAddress

abstract class DisconnectStrategy {
    var attemptNum = 0
    abstract fun onDisconnect(channel: UdpChannel, address: InetSocketAddress, requestDirection: RequestDirection): ResponseDto
}