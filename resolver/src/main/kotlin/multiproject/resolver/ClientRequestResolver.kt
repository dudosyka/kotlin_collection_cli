package multiproject.resolver

import multiproject.lib.request.Request
import multiproject.lib.request.RequestResolver
import multiproject.lib.udp.gateway.GatewayUdpChannel
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class ClientRequestResolver : RequestResolver() {
    override val gateway: GatewayUdpChannel by KoinJavaComponent.inject(GatewayUdpChannel::class.java, named("server"))
    override fun resolve(request: Request) {
        gateway.sendThrough(request) {}
    }
}