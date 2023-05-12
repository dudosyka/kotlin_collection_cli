package multiproject.lib.request.resolver

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.ExecuteException

open class ResolveError(code: ResponseCode = ResponseCode.INTERNAL_SERVER_ERROR) : ExecuteException(code) {
    override val message: String
        get() = "Request resolving failed!"
}