package multiproject.client.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.utils.ExecuteException

/**
 * Recursive script exception
 *
 * @constructor Create empty Recursive script exception
 */
class RecursiveScriptException: ExecuteException(ResponseCode.VALIDATION_ERROR) {
    override val message: String
        get() = "Recursive script cached!"
}