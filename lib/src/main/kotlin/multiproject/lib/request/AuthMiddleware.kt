package multiproject.lib.request

object AuthMiddleware: Middleware() {
    override fun invoke(): Request.() -> Unit {
        return {}
    }
}