package multiproject.lib.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.router.RouteExecutionException
import multiproject.lib.request.Request

open class MiddlewareException(override val code: ResponseCode, override val message: String, request: Request): RouteExecutionException(request = request)