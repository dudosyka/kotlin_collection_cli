package multiproject.lib.udp.server.router

import multiproject.lib.dto.command.CommandDto
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.response.Response
import multiproject.lib.exceptions.RouteNotFound
import multiproject.lib.request.Request
import multiproject.lib.utils.RequestToExecutableInterpreter

class Router {
    private val controllers: MutableList<Controller> = mutableListOf()
    private val requestInterpreter = RequestToExecutableInterpreter()
    fun addController(init: Controller.() -> Unit) {
        this.controllers.add(Controller().apply(init))
    }
    private fun getRoute(path: PathDto): Pair<Controller, Route> {
        val controller: Controller = this.controllers.find {
            it.name == path.controller
        } ?: throw RouteNotFound()

        val route: Route = controller.routes.find {
            it.name == path.route
        } ?: throw RouteNotFound()

        return Pair(controller, route)
    }
    fun getRoutes(controllerName: String): List<Route> {
        val controller: Controller = this.controllers.find {
            it.name == controllerName
        } ?: throw RouteNotFound()

        return controller.routes
    }
    fun getCommandsInfo(controllerName: String): List<CommandDto> {
        return this.getRoutes(controllerName).map {
            CommandDto(
                name = it.name,
                arguments = it.command.fields,
                hideFromClient = it.command.hideFromClient,
                fileReaderSource = it.command.fileReaderSource
            )
        }
    }

    fun run(request: Request): Response {

        val path = getRoute(request.dto.pathDto)

        path.first.middlewares.forEach {
            request.applyMiddleware(it())
        }

        path.second.middlewares.forEach {
            request.applyMiddleware(it())
        }

        return path.second.command.execute(requestInterpreter.interpret(request))
    }
}