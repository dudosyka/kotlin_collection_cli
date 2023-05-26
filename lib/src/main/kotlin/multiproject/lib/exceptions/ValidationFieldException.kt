package multiproject.lib.exceptions

import multiproject.lib.dto.command.Validator
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.command.CommandExecutionException

/**
 * Validation field exception
 *
 * @property validator
 * @constructor Create empty Validation field exception
 */
class ValidationFieldException(val validator: Validator): CommandExecutionException() {
    override val code: ResponseCode = ResponseCode.VALIDATION_ERROR
    override val message: String
        get() = "Validation failed! ${validator.describe()}"
}