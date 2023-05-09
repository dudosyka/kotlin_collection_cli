package multiproject.server.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.utils.ExecuteException

/**
 * Not unique id exception
 *
 * @constructor Create empty Not unique id exception
 */
class NotUniqueIdException: ExecuteException(ResponseCode.VALIDATION_ERROR) {
    override val message: String
        get() = "Error! Duplicate id"
}