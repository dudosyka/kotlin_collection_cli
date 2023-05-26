package multiproject.resolver

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.gateway.GatewayUdpChannel
import multiproject.lib.udp.gateway.runGateway
import multiproject.lib.udp.interfaces.OnReceive
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import java.net.InetSocketAddress

class App {
    init {
        val logger = Logger(LogLevel.INFO)
        val module = module {
            single<GatewayUdpChannel>(named("server")) {
                runGateway {
                    this.logger = logger
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
                            if (request.isEmptyPath())
                                return@run
                            requestResolver(request, true)
                        }
                    }
                    bindOn(
                        address = InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort)
                    )
                }
            }
            single<Logger>(named("logger")) {
                logger
            }
        }
        startKoin {
            modules(
                module
            )
        }
    }
}

fun main() = runBlocking(
    CoroutineExceptionHandler {
        _, error -> run {
            val logger: Logger by inject(Logger::class.java, named("logger"))
            logger(LogLevel.FATAL, "Fatal error!", Exception(error))
        }
    }
) {
    App()
    val server: GatewayUdpChannel by inject(GatewayUdpChannel::class.java, named("server"))
    server.run()
}