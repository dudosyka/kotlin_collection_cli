package multiproject.server.exceptions.execution

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.command.CommandExecutionException

/**
 * Item not found exception
 *
 * @property searchField
 * @property searchValue
 * @constructor Create empty Item not found exception
 */
class ItemNotFoundException(private val searchField: String, private val searchValue: Any): CommandExecutionException() {
    override val code: ResponseCode
        get() = ResponseCode.NOT_FOUND
    override val message: String
        get() = "Item with $searchField = $searchValue not found!"
}