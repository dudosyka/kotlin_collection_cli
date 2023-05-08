package multiproject.lib.udp.server.router

import multiproject.lib.request.Middleware

class Route {
    lateinit var name: String
    lateinit var command: Command
    val middlewares: MutableList<Middleware> = mutableListOf()
    fun addMiddleware(middleware: Middleware) {
        this.middlewares.add(middleware)
    }
}