package lab5kotlin.exceptions

class InvalidArgumentException(val argumentName: String, val validationRulesDescribe: String): Exception()