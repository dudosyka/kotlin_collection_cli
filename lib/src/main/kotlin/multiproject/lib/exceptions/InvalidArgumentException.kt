package multiproject.lib.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.utils.ExecuteException

/**
 * Invalid argument exception
 *
 * @property argumentName
 * @property validationRulesDescribe
 * @constructor Create empty Invalid argument exception
 */
class InvalidArgumentException(private val argumentName: String, val validationRulesDescribe: String): ExecuteException(ResponseCode.VALIDATION_ERROR) {
    override val message: String
        get() = "Validation failed for $argumentName: $validationRulesDescribe"
}