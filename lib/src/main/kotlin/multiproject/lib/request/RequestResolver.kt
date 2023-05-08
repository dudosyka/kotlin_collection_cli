package multiproject.lib.request

import multiproject.lib.udp.gateway.GatewayUdpChannel
import multiproject.lib.utils.ReadOnlyInitializer

abstract class RequestResolver {
    open val gateway: GatewayUdpChannel by ReadOnlyInitializer()
    abstract fun resolve(request: Request)
}