package multiproject.server.middlewares.auth

import io.jsonwebtoken.JwtException
import multiproject.lib.request.Request
import multiproject.lib.request.middleware.Middleware
import multiproject.server.exceptions.ForbiddenException
import multiproject.server.modules.user.User

object AuthMiddleware: Middleware() {

    override fun invoke(): Request.() -> Unit {
        try {
            return {
                data.arguments["user"] = User.checkToken(getHeader("token")?.toString() ?: "")
            }
        } catch (e: JwtException) {
            throw ForbiddenException()
        }
    }
}