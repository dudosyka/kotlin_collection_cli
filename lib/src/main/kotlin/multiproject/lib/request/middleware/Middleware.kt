package multiproject.lib.request.middleware

import multiproject.lib.request.Request

abstract class Middleware {
    abstract operator fun invoke(): Request.() -> Unit
}