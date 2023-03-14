package lab5kotlin.command

import lab5kotlin.exceptions.InvalidArgumentException
import lab5kotlin.collection.item.Validator

abstract class Command {
    abstract fun execute(args: List<String> = listOf()): Boolean

    fun getArgument(args: List<String?>, name: String, index: Int, validator: Validator): Any {
        if (args.size < index || args.isEmpty() || index < 0) {
            throw InvalidArgumentException(name, validator.describe(name))
        }
        val argumentValue = args[index] ?: throw InvalidArgumentException(name, validator.describe(name))
        if (!validator.validate(argumentValue))
            throw InvalidArgumentException(name, validator.describe(name))
        return validator.value!!
    }
}