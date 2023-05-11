package multiproject.lib.udp.gateway

import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.request.RequestDto
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.exceptions.NoAvailableServers
import multiproject.lib.request.Request
import multiproject.lib.sd.GatewayBalancer
import multiproject.lib.udp.UdpChannel

class GatewayUdpChannel: UdpChannel() {
    fun sendThrough(initiator: Request, updateWith: RequestDto.() -> Unit): Response {
        val data = initiator.dto.apply(updateWith)

        val serverAddress = GatewayBalancer.getServer(this) ?: throw NoAvailableServers()

        return try {
            val dto: ResponseDto = this.send(serverAddress, data)
            println("Response received: from $serverAddress with data $dto")
            this.emit(initiator.from, dto)
            Response(dto)
        } catch (e: Exception) {
            disconnectStrategy.onDisconnect(this, serverAddress, RequestDirection.FROM_SERVER)
            sendThrough(initiator) {}
        }
    }
}