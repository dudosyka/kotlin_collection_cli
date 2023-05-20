package multiproject.server.middlewares.auth

import multiproject.lib.request.Request
import multiproject.lib.request.middleware.Middleware

object BuildAuthorMiddleware: Middleware() {
    override fun invoke(): Request.() -> Unit {
        return {
            val buildUserData = this.author
            val userData = mutableMapOf(
                "id" to buildUserData["id"]!!.toLong(),
                "login" to buildUserData["login"],
                "password" to buildUserData["password"]
            )
            this.data.arguments["author"] = userData
        }
    }
}