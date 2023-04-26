package multiproject.client.exceptions

/**
 * Invalid argument exception
 *
 * @property argumentName
 * @property validationRulesDescribe
 * @constructor Create empty Invalid argument exception
 */
class InvalidArgumentException(val argumentName: String, val validationRulesDescribe: String): Exception()