package multiproject.lib.exceptions.command

import multiproject.lib.dto.response.ResponseCode

abstract class CommandExecutionException: Exception() {
    abstract val code: ResponseCode
    abstract override val message: String
}