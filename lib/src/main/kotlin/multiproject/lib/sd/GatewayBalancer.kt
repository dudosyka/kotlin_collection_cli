package multiproject.lib.sd

import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.gateway.GatewayUdpChannel
import java.net.InetSocketAddress

object GatewayBalancer {
    fun getServer(gateway: GatewayUdpChannel): InetSocketAddress? {
        gateway.servers.sortBy {
            it.pendingRequest
        }
        println("Gateway available adresses: ${gateway.servers}")
        if (gateway.servers.isEmpty())
            return null
        println("Gateway has chosen address: ${gateway.servers.first().address}")
        gateway.servers.first().pendingRequest++
        return gateway.servers.first().address
    }

    fun removeServer(gateway: UdpChannel, serverAddress: InetSocketAddress) {
        gateway.servers.removeIf {
            it.address == serverAddress
        }
    }

}