package multiproject.lib.udp.server.router

import multiproject.lib.request.middleware.Middleware

class Route {
    lateinit var name: String
    lateinit var command: Command
    val middlewares: MutableList<Middleware> = mutableListOf()
    var needAuth = true
    var authorizationEndpoint = false
    fun addMiddleware(middleware: Middleware) {
        this.middlewares.add(middleware)
    }
}