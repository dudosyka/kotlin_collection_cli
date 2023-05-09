package multiproject.resolver.resolvers

import multiproject.lib.request.Request
import multiproject.lib.request.RequestResolver
import multiproject.lib.udp.gateway.GatewayUdpChannel
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class UnknownRequestResolver : RequestResolver() {
    override val gateway: GatewayUdpChannel by inject(GatewayUdpChannel::class.java, named("server"))
    override fun resolve(request: Request) {
        gateway.emit(request.from, request.dto)
    }
}