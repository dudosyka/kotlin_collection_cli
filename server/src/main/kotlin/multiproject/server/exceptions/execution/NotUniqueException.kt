package multiproject.server.exceptions.execution

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.command.CommandExecutionException

/**
 * Not unique id exception
 *
 * @constructor Create empty Not unique id exception
 */
class NotUniqueException(private val fieldName: String): CommandExecutionException() {
    override val code: ResponseCode
        get() = ResponseCode.VALIDATION_ERROR
    override val message: String
        get() = "Error! Duplicate $fieldName"
}