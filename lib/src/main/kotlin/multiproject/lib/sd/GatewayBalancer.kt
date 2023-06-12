package multiproject.lib.sd

import multiproject.lib.dto.ConnectedServer
import multiproject.lib.udp.gateway.GatewayUdpChannel
import multiproject.lib.utils.LogLevel

object GatewayBalancer {
    fun getServer(servers: MutableList<ConnectedServer>, gateway: GatewayUdpChannel): ConnectedServer? {
        val availableServers = servers.filter { !it.temporaryUnavailable.second }.toMutableList().apply {
            this.sortBy { it.pendingRequest }
        }

        gateway.logger(LogLevel.INFO,"Gateway available addresses: $availableServers")
        if (availableServers.isEmpty())
            return null
        gateway.logger(LogLevel.INFO, "Gateway has chosen address: ${availableServers.first().address}")

        return availableServers.first()
    }
}