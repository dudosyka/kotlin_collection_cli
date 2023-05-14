package multiproject.lib.udp.gateway

object runGateway {
    operator fun invoke(init: GatewayUdpChannel.() -> Unit): GatewayUdpChannel = GatewayUdpChannel().apply(init)
}