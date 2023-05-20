package multiproject.lib.sd

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.udp.UdpChannel
import multiproject.lib.udp.gateway.GatewayUdpChannel
import multiproject.lib.utils.LogLevel
import java.net.InetSocketAddress

object GatewayBalancer {
    fun getServer(gateway: GatewayUdpChannel): ConnectedServer? {
        val availableServers = gateway.servers.filter { !it.temporaryUnavailable.second }.toMutableList().apply {
            this.sortBy { it.pendingRequest }
        }

        gateway.logger(LogLevel.INFO,"Gateway available addresses: $availableServers")
        if (availableServers.isEmpty())
            return null
        gateway.logger(LogLevel.INFO, "Gateway has chosen address: ${availableServers.first().address}")
        gateway.servers.find { availableServers.first().address == it.address }!!.pendingRequest++
        return availableServers.first()
    }

    fun removeServer(gateway: UdpChannel, serverAddress: InetSocketAddress) {
        gateway.servers.removeIf {
            it.address == serverAddress
        }
    }
}