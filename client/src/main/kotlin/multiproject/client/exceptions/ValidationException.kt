package multiproject.client.exceptions

import multiproject.lib.dto.command.Validator
import multiproject.lib.exceptions.client.ClientExecutionException

class ValidationException(val validator: Validator): ClientExecutionException() {
    override val message: String
        get() = "Validation failed! ${validator.describe()}"
}