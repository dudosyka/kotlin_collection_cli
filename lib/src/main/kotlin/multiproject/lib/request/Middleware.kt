package multiproject.lib.request

abstract class Middleware {
    abstract operator fun invoke(): Request.() -> Unit
}