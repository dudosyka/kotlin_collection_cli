package multiproject.lib.udp.disconnect

import multiproject.lib.dto.ResponseCode
import multiproject.lib.dto.ResponseDto
import multiproject.lib.udp.UdpChannel
import java.net.InetSocketAddress

class CloseOnDisconnectStrategy: DisconnectStrategy() {
    override fun onDisconnect(channel: UdpChannel, address: InetSocketAddress): ResponseDto {
        return ResponseDto(ResponseCode.CONNECTION_REFUSED, "Server unavailable")
    }
}