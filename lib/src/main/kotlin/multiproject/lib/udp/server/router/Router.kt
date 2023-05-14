package multiproject.lib.udp.server.router

import multiproject.lib.dto.command.CommandDto
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.ExecuteException
import multiproject.lib.exceptions.RouteNotFound
import multiproject.lib.request.Request
import multiproject.lib.request.RequestToExecutableInterpreter
import multiproject.lib.request.middleware.MiddlewareException
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger

class Router(val logger: Logger) {
    private val controllers: MutableList<Controller> = mutableListOf()
    private val requestInterpreter = RequestToExecutableInterpreter()
    fun addController(init: Controller.() -> Unit) {
        this.controllers.add(Controller().apply(init))
    }
    private fun getRoute(path: PathDto): Pair<Controller, Route> {
        val controller: Controller = this.controllers.find {
            it.name == path.controller
        } ?: throw RouteNotFound(path)

        val route: Route = controller.routes.find {
            it.name == path.route
        } ?: throw RouteNotFound(path)

        return Pair(controller, route)
    }

    fun getRoutes(controllerName: String): List<Route> {
        val controller: Controller = this.controllers.find {
            it.name == controllerName
        } ?: throw RouteNotFound(PathDto(controllerName, ""))

        return controller.routes
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
                    fileReaderSource = it.command.fileReaderSource
                )
            }
        }
    }

    fun run(request: Request): Response {

        val path = getRoute(request.path)

        return try {
            path.first.middlewares.forEach {
                request.applyMiddleware(it())
            }

            path.second.middlewares.forEach {
                request.applyMiddleware(it())
            }

            path.second.command.execute(requestInterpreter.interpret(request))
        } catch (e: Exception) {
            logger(LogLevel.FATAL, "Fatal error!", error = e)
            Response(ResponseCode.INTERNAL_SERVER_ERROR, "$e")
        } catch (e: MiddlewareException) {
            logger(LogLevel.INFO, "Middleware exception!", error = e)
            Response(ResponseCode.BAD_REQUEST, e.message)
        } catch (e: ExecuteException) {
            logger(LogLevel.INFO, "Execute exception!", error = e)
            Response(e.code, e.message)
        }
    }
}