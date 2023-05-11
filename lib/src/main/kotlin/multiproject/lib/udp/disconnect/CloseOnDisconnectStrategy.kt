package multiproject.lib.udp.disconnect

import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.sd.GatewayBalancer
import multiproject.lib.udp.UdpChannel
import java.net.InetSocketAddress

class CloseOnDisconnectStrategy: DisconnectStrategy() {
    override fun onDisconnect(channel: UdpChannel, address: InetSocketAddress, requestDirection: RequestDirection): ResponseDto {
        GatewayBalancer.removeServer(channel, address)
        return ResponseDto(ResponseCode.CONNECTION_REFUSED, "Server unavailable")
    }
}