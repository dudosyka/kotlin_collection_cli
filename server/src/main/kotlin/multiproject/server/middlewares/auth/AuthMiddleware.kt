package multiproject.server.middlewares.auth

import io.jsonwebtoken.JwtException
import multiproject.lib.request.Request
import multiproject.lib.request.middleware.Middleware
import multiproject.server.exceptions.ForbiddenException
import multiproject.server.modules.user.auth.Auth

object AuthMiddleware: Middleware() {

    override fun invoke(): Request.() -> Unit {
        try {
            val authModule = Auth()
            return {
                data.arguments["user"] = authModule.auth(getHeader("token")?.toString() ?: "")
            }
        } catch (e: JwtException) {
            throw ForbiddenException()
        }
    }
}