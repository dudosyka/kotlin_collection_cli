package multiproject.resolver

import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.gateway.GatewayUdpChannel
import multiproject.lib.udp.gateway.runGateway
import multiproject.lib.udp.interfaces.OnReceive
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import java.net.InetSocketAddress

class App {
    init {
        val module = module {
            single<GatewayUdpChannel>(named("server")) {
                runGateway {
                    requestResolver = GatewayRequestResolver()
                    receiveCallback = OnReceive { address, request ->
                        run {
                            if (request.isEmptyPath())
                                return@run

                            request setSender address
                            requestResolver(request)
                        }
                    }
                    firstConnectCallback = OnReceive { _, request ->
                        run {
                            requestResolver(request, true)
                        }
                    }
                    bindOn(
                        address = InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort)
                    )
                }
            }
        }
        startKoin {
            modules(
                module
            )
        }
    }
}


fun main() {
    try {
        App()
        val server: GatewayUdpChannel by inject(GatewayUdpChannel::class.java, named("server"))
        server.run()
    } catch (e: Exception) {
        println("Fatal error! ${e.message}")
    }
}