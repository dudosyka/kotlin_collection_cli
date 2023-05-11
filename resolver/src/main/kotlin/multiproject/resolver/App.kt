package multiproject.resolver

import multiproject.lib.request.Request
import multiproject.lib.udp.SocketAddressInterpreter
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
                    receiveCallback = OnReceive { from, requestData ->
                        run {
                            if (requestData.pathDto.controller == "")
                                return@run

                            val inetAddress = SocketAddressInterpreter.interpret(from)
                            val request = Request(requestData, inetAddress)

                            requestResolver(request)
                        }
                    }
                    firstConnectCallback = OnReceive { from, requestData ->
                        run {

                            val inetAddress = SocketAddressInterpreter.interpret(from)
                            val request = Request(requestData, inetAddress)

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