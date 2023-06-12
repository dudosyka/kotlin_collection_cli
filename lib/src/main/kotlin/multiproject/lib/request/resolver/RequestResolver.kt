package multiproject.lib.request.resolver

import multiproject.lib.exceptions.gateway.ResolveError
import multiproject.lib.request.Request

abstract class RequestResolver {
    abstract suspend fun resolveFirst(request: Request)
    abstract suspend fun resolve(request: Request)
    abstract fun resolveError(request: Request, e: ResolveError)
    suspend operator fun invoke(request: Request, first: Boolean = false) {
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