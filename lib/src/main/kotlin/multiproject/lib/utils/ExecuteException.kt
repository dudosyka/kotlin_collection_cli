package multiproject.lib.utils

import multiproject.lib.dto.response.ResponseCode

abstract class ExecuteException(val code: ResponseCode = ResponseCode.BAD_REQUEST): Exception() {
    abstract override val message: String
}