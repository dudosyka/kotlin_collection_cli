package multiproject.lib.udp.server.router

import multiproject.lib.request.middleware.Middleware

class Controller {
    lateinit var name: String
    val routes: MutableList<Route> = mutableListOf()
    val middlewares: MutableList<Middleware> = mutableListOf()
    var needAuth = true
    fun addMiddleware(middleware: Middleware) {
        this.middlewares.add(middleware)
    }
    fun addRoute(init: Route.() -> Unit) {
        this.routes.add(Route().apply(init))
    }
}