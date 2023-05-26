package multiproject.server.exceptions.execution

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.command.CommandExecutionException

class FileDumpException(val parent: Exception, val filePath: String, override val message: String): CommandExecutionException() {
    override val code: ResponseCode
        get() = ResponseCode.INTERNAL_SERVER_ERROR
}