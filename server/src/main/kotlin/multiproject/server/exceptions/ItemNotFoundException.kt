package multiproject.server.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.utils.ExecuteException

/**
 * Item not found exception
 *
 * @property searchField
 * @property searchValue
 * @constructor Create empty Item not found exception
 */
class ItemNotFoundException(private val searchField: String, private val searchValue: Any): ExecuteException(ResponseCode.NOT_FOUND) {
    override val message: String
        get() = "Item with $searchField = $searchValue not found!"
}