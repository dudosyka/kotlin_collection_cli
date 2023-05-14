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
                val user = User.checkToken(getHeader("token")?.toString() ?: "")
                data.arguments["user"] = user.subject
                data.arguments["__buildUserData"] = user.data
            }
        } catch (e: JwtException) {
            throw ForbiddenException()
        }
    }
}