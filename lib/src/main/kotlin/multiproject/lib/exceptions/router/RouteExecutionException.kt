package multiproject.lib.exceptions.router

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.request.Request

abstract class RouteExecutionException(val request: Request): Exception() {
    abstract val code: ResponseCode
    abstract override val message: String
}