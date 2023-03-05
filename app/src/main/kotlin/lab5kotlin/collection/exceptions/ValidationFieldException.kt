package lab5kotlin.collection.exceptions

class ValidationFieldException(val propertyName: String, val validatedValue: Any?) : Exception()