/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package multiproject.server

import kotlinx.coroutines.*
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.request.Request
import multiproject.lib.udp.interfaces.OnReceive
import multiproject.lib.udp.server.ServerUdpChannel
import multiproject.lib.udp.server.runServer
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import multiproject.lib.utils.SocketAddressInterpreter
import multiproject.server.collection.Collection
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.command.*
import multiproject.server.command.system.SystemDumpCommand
import multiproject.server.command.system.SystemLoadCommand
import multiproject.server.command.user.AuthCommand
import multiproject.server.command.user.LongCommand
import multiproject.server.command.user.SignupCommand
import multiproject.server.database.DatabaseManager
import multiproject.server.dump.DumpManager
import multiproject.server.dump.PostgresqlDumpManager
import multiproject.server.middlewares.auth.AuthMiddleware
import multiproject.server.middlewares.auth.BuildAuthorMiddleware
import multiproject.server.modules.flat.Flat
import multiproject.server.modules.flat.FlatBuilder
import multiproject.server.modules.flat.FlatCollection
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject


class App {
    init {
        val logger = Logger(LogLevel.DEBUG)
        val collection = FlatCollection(mutableListOf(), FlatBuilder())
        val module = module {
            single<Collection<Flat>>(named("collection")) {
                collection
            }
            single<DumpManager<Flat>>(named("dumpManager")) {
                PostgresqlDumpManager(FlatBuilder())
            }
            single<EntityBuilder<Flat>>(named("builder")) {
                FlatBuilder()
            }
            single<DatabaseManager>(named("dbManager")) {
                DatabaseManager()
            }
            single<Logger>(named("logger")) {
                logger
            }
            single<ServerUdpChannel>(named("server")) {
                runServer {
                    this.logger = logger
                    applyRouter {
                        scope = CoroutineScope(Job())
                        addController {
                            name = "collection"
                            addMiddleware(AuthMiddleware)
                            addRoute {
                                name = "help"
                                command = HelpCommand(this@addController)
                            }
                            addRoute {
                                name = "info"
                                command = InfoCommand(this@addController)
                            }
                            addRoute {
                                name = "add"
                                command = AddCommand(this@addController)
                                addMiddleware(BuildAuthorMiddleware)
                            }
                            addRoute {
                                name = "show"
                                command = ShowCommand(this@addController)
                            }
                            addRoute {
                                name = "update"
                                command = UpdateCommand(this@addController)
                                addMiddleware(BuildAuthorMiddleware)
                            }
                            addRoute {
                                name = "remove_by_id"
                                command = RemoveByIdCommand(this@addController)
                                addMiddleware(BuildAuthorMiddleware)
                            }
//                            addRoute {
//                                name = "load"
//                                command = LoadCommand(this@addController)
//                            }
                            addRoute {
                                name = "execute_script"
                                command = ExecuteScriptCommand(this@addController)
                            }
                            addRoute {
                                name = "remove_at"
                                command = RemoveAtCommand(this@addController)
                                addMiddleware(BuildAuthorMiddleware)
                            }
                            addRoute {
                                name = "add_if_max"
                                command = AddIfMaxCommand(this@addController)
                                addMiddleware(BuildAuthorMiddleware)
                            }
                            addRoute {
                                name = "reorder"
                                command = ReorderCommand(this@addController)
                            }
                            addRoute {
                                name = "count_by_number_of_rooms"
                                command = CountByNumberOfRoomsCommand(this@addController)
                            }
                            addRoute {
                                name = "count_less_than_time_to_metro_by_transport"
                                command = CountLessThanTimeToMetroByTransportCommand(this@addController)
                            }
                            addRoute {
                                name = "filter_less_than_furnish"
                                command = FilterLessThanFurnish(this@addController)
                            }
                        }
                        addController {
                            name = "system"
                            needAuth = false
                            addRoute {
                                name = "_load"
                                command = SystemLoadCommand(this@addController)
                            }
                            addRoute {
                                name = "_dump"
                                command = SystemDumpCommand(this@addController)
                            }
                        }
                        addController {
                            name = "user"
                            addRoute {
                                name = "auth"
                                authorizationEndpoint = true
                                command = AuthCommand(this@addController)
                                needAuth = false
                            }
                            addRoute {
                                name = "long"
                                command = LongCommand(this@addController)
                                needAuth = false
                            }
                            addRoute {
                                name = "signup"
                                command = SignupCommand(this@addController)
                                needAuth = false
                            }
//                            addRoute {
//                                name = "get"
//                                command = GetByTokenCommand(this@addController)
//                                addMiddleware(AuthMiddleware)
//                            }
                            addRoute {
                                name = "help"
                                command = HelpCommand(this@addController)
                                needAuth = false
                            }
                        }
                    }
                    receiveCallback = OnReceive {
                        address, request -> withContext(Dispatchers.Default) {
                            if (request.isEmptyPath())
                                return@withContext

                            if (request.path.controller == "_system" && request.path.route == "sync") {
                                launch { collection.pull(request.getSyncHelper().commits) }.join()
                                emitCache(collection.getInfo().size.toDouble())
                                return@withContext
                            }

                            requestsChannel.send(Pair(address, request))
                        }
                    }
                    bindOn(
                        address = null
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

@OptIn(ExperimentalCoroutinesApi::class)
fun main(): Unit = runBlocking (
    CoroutineExceptionHandler {
        _, error -> run {
            println("Caught here!")
            val logger: Logger by inject(Logger::class.java, named("logger"))
            logger(LogLevel.FATAL, "Fatal error!", Exception(error))
        }
    }
) {
    App()
    val server: ServerUdpChannel by inject(ServerUdpChannel::class.java, named("server"))
    val collection: Collection<Flat> by inject(Collection::class.java, named("collection"))
    collection.loadDump()
    server.bindToResolver()
    server.emitCache(collection.getInfo().size.toDouble())
    withContext(Dispatchers.Default) {
        launch {
            server.run()
        }

        launch {
            while (true) {
                val tryToGet = server.requestsChannel.tryReceive()

                if (tryToGet.isSuccess) {

                    val item = tryToGet.getOrNull()!!
                    val address = item.first
                    val request = item.second

                    val syncType = request.getSyncType()
                    val syncHelper = request.getSyncHelper()
                    if (syncType.sync) {
                        val commits = request.getSyncHelper().commits.map { it }

                        launch {
                            collection.pullAndDump(commits)
                            server.emitCache(collection.getInfo().size.toDouble())
                        }
                        syncHelper.servers.forEach {
                            server.emit(it!!, Request(PathDto("_system", "sync")).apply { this setSyncHelper request.getSyncHelper() })
                        }
                        request setSyncHelper (request.getSyncHelper().apply {
                            this.commits = mutableListOf()
                        })
                    }

                    val responseChannelItem = ServerUdpChannel.ResponseChannelItem(
                        from = address,
                        request = request,
                        response = server.router.run(
                            request,
                            onError = ResponseDto(ResponseCode.INTERNAL_SERVER_ERROR, "Resolver error")
                        )
                    )

                    server.responseChannel.send(responseChannelItem)
                }
            }
        }

        launch {
            while (true) {
                val tryToGet = server.responseChannel.tryReceive()

                if (tryToGet.isSuccess) {
                    val responseChannelItem = tryToGet.getOrNull()!!
                    val request = responseChannelItem.request
                    val scope = CoroutineScope(Job())

                    scope.launch {
                        val response = responseChannelItem.response.await()
                        val address = responseChannelItem.from

                        val syncType = request.getSyncType()

                        request.apply {
                            this.response = response.dto!!

                            this setDirection RequestDirection.FROM_SERVER
                            this setSender server.getChannelAddress()

                            this setSyncHelper (this.getSyncHelper().apply {
                                if (syncType.sync)
                                    this.synchronizationEnded = true
                                this.commits.addAll(response.commits)
                            })
                        }

                        server.emit(
                            SocketAddressInterpreter.interpret(address),
                            request
                        )
                    }
                }
            }
        }
    }
}