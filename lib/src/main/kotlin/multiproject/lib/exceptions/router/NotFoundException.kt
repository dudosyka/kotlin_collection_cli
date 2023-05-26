package multiproject.lib.exceptions.router

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.request.Request

class NotFoundException(request: Request) : RouteExecutionException(request) {
    override val code: ResponseCode
        get() = ResponseCode.NOT_FOUND
    override val message: String
        get() = "Route ${request.path.controller}/${request.path.route} not found!"
}