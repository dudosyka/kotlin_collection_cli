/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package multiproject.server

import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDirection
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.request.Request
import multiproject.lib.utils.SocketAddressInterpreter
import multiproject.lib.udp.interfaces.OnReceive
import multiproject.lib.udp.server.ServerUdpChannel
import multiproject.lib.udp.server.runServer
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger
import multiproject.server.collection.Collection
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.command.*
import multiproject.server.command.system.SystemDumpCommand
import multiproject.server.command.system.SystemLoadCommand
import multiproject.server.command.user.AuthCommand
import multiproject.server.command.user.GetByTokenCommand
import multiproject.server.command.user.LongCommand
import multiproject.server.database.DatabaseManager
import multiproject.server.dump.DumpManager
import multiproject.server.dump.PostgresqlDumpManager
import multiproject.server.modules.flat.Flat
import multiproject.server.modules.flat.FlatBuilder
import multiproject.server.modules.flat.FlatCollection
import multiproject.server.middlewares.auth.AuthMiddleware
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject

class App () {
    init {
        val logger = Logger(LogLevel.INFO)
        val module = module {
            single<Collection<Flat>>(named("collection")) {
                FlatCollection(mutableListOf())
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
                    applyRouter {
                        addController {
                            name = "collection"
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
                            }
                            addRoute {
                                name = "show"
                                command = ShowCommand(this@addController)
                            }
                            addRoute {
                                name = "update"
                                command = UpdateCommand(this@addController)
                            }
                            addRoute {
                                name = "remove_by_id"
                                command = RemoveByIdCommand(this@addController)
                            }
                            addRoute {
                                name = "clear"
                                command = ClearCommand(this@addController)
                            }
                            addRoute {
                                name = "load"
                                command = LoadCommand(this@addController)
                            }
                            addRoute {
                                name = "execute_script"
                                command = ExecuteScriptCommand(this@addController)
                            }
                            addRoute {
                                name = "remove_at"
                                command = RemoveAtCommand(this@addController)
                            }
                            addRoute {
                                name = "add_if_max"
                                command = AddIfMaxCommand(this@addController)
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
                                name = "get"
                                command = GetByTokenCommand(this@addController)
                                addMiddleware(AuthMiddleware)
                            }
                            addRoute {
                                name = "help"
                                command = HelpCommand(this@addController)
                                needAuth = false
                            }
                        }
                    }
                    receiveCallback = OnReceive {
                        address, request -> run {
                            if (request.isEmptyPath())
                                return@run

                            val response = router.run(request).dto ?: ResponseDto(ResponseCode.INTERNAL_SERVER_ERROR, "Resolver error")

                            request.apply {
                                this.response = response
                                this setDirection RequestDirection.FROM_SERVER
                            }

                            this.emit(
                                SocketAddressInterpreter.interpret(address),
                                request
                            )
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


fun main() {
    val server: ServerUdpChannel by inject(ServerUdpChannel::class.java, named("server"))
    val collection: Collection<Flat> by inject(Collection::class.java, named("collection"))
    try {
        App()
        collection.loadDump()
        server.run()
    } finally {
        server.selfExecute(Request(PathDto("system", "_dump")))
    }
}