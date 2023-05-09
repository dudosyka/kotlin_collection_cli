package multiproject.resolver.resolvers.server

import multiproject.lib.request.Request
import multiproject.lib.request.RequestResolver

class ServerRequestResolver : RequestResolver() {
    override fun resolve(request: Request) {
        val result = gateway.send(request.from, request.dto)
        print("Result returned! $result")
    }
}