package multiproject.tests

import kotlinx.coroutines.*
import multiproject.client.command.CommandResolver
import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.command.CommandDto
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDataDto
import multiproject.lib.request.Request
import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.client.ClientUdpChannel
import multiproject.lib.udp.client.runClient
import multiproject.lib.udp.disconnect.RestoreOnDisconnectStrategy
import multiproject.lib.udp.interfaces.OnConnectionRefused
import multiproject.lib.udp.interfaces.OnConnectionRestored
import multiproject.lib.udp.interfaces.OnDisconnectAttempt
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import java.net.InetSocketAddress
import java.time.ZonedDateTime

class App {
    lateinit var commands: Map<String, List<CommandDto>>
    init {
        val logger = Logger(LogLevel.FATAL)
        val module = module {
            single<Logger>(named("logger")) {
                logger
            }
            factory <ClientUdpChannel>(named("client")) {
                runClient {
                    this.logger = logger
                    defaultController = "collection"
                    onConnectionRestoredCallback = OnConnectionRestored {
                            response -> run {
                        CommandResolver.updateCommandList(response.commands)
                    }
                    }
                    onConnectionRefusedCallback = OnConnectionRefused {
                        println("Connection lost")
                    }
                    onDisconnectAttempt = OnDisconnectAttempt {
                            attemptNum -> println("Try to reconnect... Reconnect attempt #$attemptNum")
                    }
                    addServer(
                        address = ConnectedServer(0, lastRequest = ZonedDateTime.now().toEpochSecond(), InetSocketAddress(
                            UdpConfig.serverAddress, UdpConfig.serverPort)
                        )
                    )
                    disconnectStrategy = RestoreOnDisconnectStrategy()
                    bindOn(null)
                }
            }
        }
        startKoin {
            modules(
                module
            )
        }
    }

    fun getCommandByName(controller: String, name: String): CommandDto? {
        val commands = this.commands[controller]?.filter { it.name == name } ?: listOf()
        return if (commands.isNotEmpty())
            commands.first()
        else
            null
    }

    fun createClient(login: String, password: String): ClientUdpChannel {
        val client: ClientUdpChannel by inject(ClientUdpChannel::class.java, named("client"))
        var result = client.sendRequest(Request(path = PathDto("system", "_load")))
        val authResult = client.sendRequest(Request(path = PathDto("user", "auth"), data = RequestDataDto(
            arguments = mutableMapOf(
                "login" to login,
                "password" to password
            )
        )))
        client.auth(authResult.result)
        result = client.sendRequest(Request(path = PathDto("system", "_load")))
        commands = result.commands.map { controller -> controller.key to controller.value }.toMap()
        return client
    }
}

fun main(): Unit = runBlocking(
    CoroutineExceptionHandler {
        _, error -> run {
            val logger: Logger by inject(Logger::class.java, named("logger"))
            logger(LogLevel.FATAL, "Fatal error!", Exception(error))
        }
    }
) {
    val app = App()
    withContext(Dispatchers.Default) {
        val main = app.createClient("dudosyka", "test")
        repeat(3) {
            clientNumber -> launch {
                val client = app.createClient("dudosyka", "test")

                repeat(1) {

                    val request = Request(
                        path = PathDto("collection", "add"),
                        data = RequestDataDto(
                            arguments = mutableMapOf(
                                "name" to "name",
                                "area" to 12.0,
                                "numberOfRooms" to 3,
                                "numberOfBathrooms" to 2,
                                "timeToMetroByTransport" to 12,
                                "coordinates" to mutableMapOf(
                                    "x" to 1,
                                    "y" to 3,
                                ),
                                "house" to mutableMapOf(
                                    "name" to "name",
                                    "year" to 123,
                                    "numberOfFloors" to 4,
                                    "numberOfFlatsOnFloor" to 123,
                                    "numberOfLifts" to 23,
                                ),
                                "furnish" to "NONE"
                            )
                        )
                    )

                    request setSyncType app.getCommandByName("collection", "add")!!.commandSyncType

                    val result = client.sendRequest(request)

                    println("client #$clientNumber, returned request #$it: $result")

//                    if (it % 70 == 0) {
//                        val requestInfo = Request(path = PathDto("collection", "info"))
//                        requestInfo setSyncType app.getCommandByName("collection", "info")!!.commandSyncType
//                        val show = client.sendRequest(requestInfo)
//                        println(show.result)
//                    }

                }

                val requestInfo = Request(path = PathDto("collection", "info"))
                requestInfo setSyncType app.getCommandByName("collection", "info")!!.commandSyncType
                val show = main.sendRequest(requestInfo)
                println(show.result)
            }
        }
    }
}