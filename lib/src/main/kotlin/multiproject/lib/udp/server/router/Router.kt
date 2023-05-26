package multiproject.lib.udp.server.router

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import multiproject.lib.dto.command.CommandDto
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.exceptions.InvalidSocketAddress
import multiproject.lib.exceptions.MiddlewareException
import multiproject.lib.exceptions.command.CommandExecutionException
import multiproject.lib.exceptions.router.NotFoundException
import multiproject.lib.exceptions.router.RouteExecutionException
import multiproject.lib.request.Request
import multiproject.lib.request.RequestToExecutableInterpreter
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger

class Router(val logger: Logger) {
    private val controllers: MutableList<Controller> = mutableListOf()
    private val requestInterpreter = RequestToExecutableInterpreter()
    lateinit var scope: CoroutineScope
    fun addController(init: Controller.() -> Unit) {
        this.controllers.add(Controller().apply(init))
    }
    private fun getRoute(request: Request): Pair<Controller, Route> {
        val controller: Controller = this.controllers.find {
            it.name == request.path.controller
        } ?: throw NotFoundException(request)

        val route: Route = controller.routes.find {
            it.name == request.path.route
        } ?: throw NotFoundException(request)

        return Pair(controller, route)
    }

    fun getRoutes(controllerName: String): List<Route> {
        val controller: Controller? = this.controllers.find {
            it.name == controllerName
        }

        return controller?.routes ?: listOf()
    }
    fun getCommandsInfo(): Map<String, List<CommandDto>> {
        return this.controllers.associate { controller ->
            controller.name to this.getRoutes(controller.name).map {
                CommandDto(
                    name = it.name,
                    needAuth = controller.needAuth && it.needAuth,
                    authorizedEndpoint = it.authorizationEndpoint,
                    arguments = it.command.fields,
                    hideFromClient = it.command.hideFromClient,
                    fileReaderSource = it.command.fileReaderSource,
                    commandSyncType = it.command.commandSyncType
                )
            }
        }
    }

    fun run(request: Request, onError: ResponseDto? = null): Deferred<Response> = scope.async {
        val path = getRoute(request)
        return@async try {
            path.first.middlewares.forEach {
                request.applyMiddleware(it())
            }

            path.second.middlewares.forEach {
                request.applyMiddleware(it())
            }

            path.second.command.execute(requestInterpreter.interpret(request)).apply {
                if (dto == null)
                    dto = onError
            }
        } catch (e: CommandExecutionException) {
            logger(LogLevel.FATAL, "Command execution exception!", error = e)
            Response(e.code, e.message)
        } catch (e: RouteExecutionException) {
            logger(LogLevel.INFO, "Route execution exception!", error = e)
            Response(e.code, e.message)
        } catch (e: MiddlewareException) {
            logger(LogLevel.INFO, "Middleware exception!", error = e)
            Response(e.code, e.message)
        } catch (e: InvalidSocketAddress) {
            logger(LogLevel.ERROR, "Error socket address parsing!")
            Response(ResponseCode.BAD_REQUEST, "Address failed!")
        } catch (e: Exception) {
            logger(LogLevel.ERROR, "Fatal error!", error = e)
            Response(ResponseCode.INTERNAL_SERVER_ERROR, "$e")
        }
    }
}