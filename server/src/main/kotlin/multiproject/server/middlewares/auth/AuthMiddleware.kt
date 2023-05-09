package multiproject.server.middlewares.auth

import io.jsonwebtoken.JwtException
import multiproject.lib.request.Request
import multiproject.lib.request.middleware.Middleware
import multiproject.server.exceptions.ForbiddenException
import multiproject.server.modules.auth.Auth

object AuthMiddleware: Middleware() {

    override fun invoke(): Request.() -> Unit {
        try {
            val authModule = Auth()
            return {
                dto.data.arguments["user"] = authModule.auth(dto.headers["token"]?.toString() ?: "")
            }
        } catch (e: JwtException) {
            throw ForbiddenException()
        }
    }
}