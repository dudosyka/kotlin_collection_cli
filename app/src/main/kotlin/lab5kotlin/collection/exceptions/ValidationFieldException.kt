package lab5kotlin.collection.exceptions

import lab5kotlin.collection.item.Field

class ValidationFieldException(field: Field, validatedValue: Any?) : Exception() {
    val field: Field
    val validatedValue: Any?

    init {
        this.field = field
        this.validatedValue = validatedValue
    }
}