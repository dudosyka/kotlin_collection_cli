package multiproject.lib.request.resolvers

import multiproject.lib.dto.request.RequestDto
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.sd.GatewayBalancer
import multiproject.lib.udp.SocketAddressInterpreter
import multiproject.lib.udp.gateway.GatewayUdpChannel
import java.net.InetSocketAddress

class ClientRequestResolver(gateway: GatewayUdpChannel, from: InetSocketAddress) : RequestResolver(gateway, from) {
    override fun resolve(requestDto: RequestDto?) {
        val address = GatewayBalancer.getServer(gateway);
        if (address == null) {
            gateway.emit(
                from,
                ResponseDto(ResponseCode.CONNECTION_REFUSED, "No available servers. Try to reconnect later.")
            )
            return;
        }
        requestDto!!.headers["client"] = from.toString();
        val response = gateway.send(address, requestDto)
        GatewayBalancer.dropPendingRequest(gateway, address);
        println("Response received: from $address with data $response");
        val clientAddress = SocketAddressInterpreter.interpret(response.headers["client"].toString());
        gateway.emit(clientAddress, response);
    }
}