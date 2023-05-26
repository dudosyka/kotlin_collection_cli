package multiproject.lib.request.resolver

import multiproject.lib.exceptions.gateway.ResolveError
import multiproject.lib.request.Request

abstract class RequestResolver {
    abstract fun resolveFirst(request: Request)
    abstract fun resolve(request: Request)
    abstract fun resolveError(request: Request, e: ResolveError)
    operator fun invoke(request: Request, first: Boolean = false) {
        try {
            if (first)
                resolveFirst(request)
            else
                resolve(request)
        } catch (e: ResolveError) {
            resolveError(request, e)
        }
    }
}