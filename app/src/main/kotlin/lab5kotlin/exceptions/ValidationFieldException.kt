package lab5kotlin.exceptions

import lab5kotlin.collection.item.Validator

class ValidationFieldException(private val propertyName: String, val validator: Validator) : Exception() {
    override val message: String
        get() = "Validation failed! ${validator.describe(propertyName)}"
}