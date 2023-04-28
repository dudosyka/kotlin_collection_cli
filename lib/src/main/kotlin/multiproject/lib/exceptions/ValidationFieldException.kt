package multiproject.lib.exceptions

import multiproject.lib.dto.command.Validator

/**
 * Validation field exception
 *
 * @property propertyName
 * @property validator
 * @constructor Create empty Validation field exception
 */
class ValidationFieldException(private val propertyName: String, val validator: Validator) : Exception() {
    override val message: String
        get() = "Validation failed! ${validator.describe()}"
}