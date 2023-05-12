package multiproject.server.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.ExecuteException

/**
 * Not unique id exception
 *
 * @constructor Create empty Not unique id exception
 */
class NotUniqueException(private val fieldName: String): ExecuteException(ResponseCode.VALIDATION_ERROR) {
    override val message: String
        get() = "Error! Duplicate $fieldName"
}