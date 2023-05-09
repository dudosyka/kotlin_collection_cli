package multiproject.lib.request.middleware

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.utils.ExecuteException

open class MiddlewareException(code: ResponseCode, override val message: String): ExecuteException(code)