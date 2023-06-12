package multiproject.resolver

import kotlinx.coroutines.*
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
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
        val logger = Logger(LogLevel.DEBUG)
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
                    firstConnectCallback = OnReceive { address, request ->
                        run {
                            if (request.isEmptyPath())
                                return@run

                            request setSender address

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

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun main(): Unit = runBlocking(
    CoroutineExceptionHandler {
        _, error -> run {
            val logger: Logger by inject(Logger::class.java, named("logger"))
            logger(LogLevel.FATAL, "Fatal error!", Exception(error))
        }
    }
) {
    App()
    val server: GatewayUdpChannel by inject(GatewayUdpChannel::class.java, named("server"))
    withContext(Dispatchers.Default) {
        launch {
            server.run()
        }

        //Manage requests which come from clients
        launch {
            while (true) {
                if (!server.clientRequestsChannel.isEmpty && !server.clientRequestsChannel.isClosedForReceive) {
                    val syncState = server.syncState()
//                    println("Check stuck! ${syncState.blocked}")
                    //If we sync now we stop processing requests
                    if (syncState.blocked) {
                        continue
                    }

                    val msg = server.clientRequestsChannel.receive()

                    println(msg)

                    val request = msg.second
                    server.sendThrough(request) {}
                }
            }
        }

        //Manage request which came from servers
        launch {
            while (true) {
                for (msg in server.requestsChannel) {
                    val request = msg.second
                    val syncState = server.syncState()
                    if (!(server clearPending request)) {
                        continue
                    }
                    server.unblockServers(request.getSender())

                    val syncHelper = request.getSyncHelper()

                    println(syncState)
                    println(request.getFrom())
                    println(syncState.initiator?.getFrom())

                    if (syncState.blocked && request.getFrom() == syncState.initiator?.getFrom()) {
                        println("Sync stopped!")
                        server.stopSync()
                    }

                    if (syncHelper.commits.size > 0)
                        server.addCommits(syncHelper.commits)

                    val commits = server.getCommits()

                    server.logger(LogLevel.INFO, "Unpushed changes $commits")
                    val from = request.getFrom()
                    request.removeSystemHeaders()
                    server.emit(from, request)
                }
            }
        }

        launch {
            while (true) {
                for (msg in server.failedRequestsChannel) {
                    val e = msg.first
                    if (e.code == ResponseCode.CONNECTION_REFUSED) {
                        msg.second.apply {
                            response = ResponseDto(e.code, result = "Server unavailable. Connection refused.")
                        }
                        server.emit(msg.second.getFrom(), msg.second)
                    } else {
                        server.emit(msg.second.getFrom(), msg.second.apply {
                            response = ResponseDto(e.code, result = "Server error!")
                        })
                    }
                }
            }
        }

        println("Now we know that our coroutines works!")

    }
}