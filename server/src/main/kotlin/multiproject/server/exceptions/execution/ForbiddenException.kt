package multiproject.server.exceptions.execution

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.command.CommandExecutionException

class ForbiddenException(override val message: String): CommandExecutionException() {
    override val code: ResponseCode
        get() = ResponseCode.FORBIDDEN
}