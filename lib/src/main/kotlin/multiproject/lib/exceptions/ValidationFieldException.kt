package multiproject.lib.exceptions

import multiproject.lib.dto.command.Validator
import multiproject.lib.dto.response.ResponseCode

/**
 * Validation field exception
 *
 * @property propertyName
 * @property validator
 * @constructor Create empty Validation field exception
 */
class ValidationFieldException(private val propertyName: String, val validator: Validator) : ExecuteException(ResponseCode.VALIDATION_ERROR) {
    override val message: String
        get() = "Validation failed! ${validator.describe()}"
}