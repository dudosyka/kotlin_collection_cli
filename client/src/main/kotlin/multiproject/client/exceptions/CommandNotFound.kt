package multiproject.client.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.utils.ExecuteException

class CommandNotFound(private val controller: String, private val commandName: String): ExecuteException(ResponseCode.NOT_FOUND) {
    override val message: String
        get() = "Command with name $commandName not found in $controller controller!"
}