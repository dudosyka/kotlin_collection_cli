package lab5kotlin.command

import lab5kotlin.exceptions.InvalidArgumentException
import lab5kotlin.collection.item.Validator

/**
 * Command
 *
 * @constructor Create empty Command
 */
abstract class Command {
    open val needObject = false
    open val fields: Map<String, Validator> = mapOf()
    /**
     * Execute
     *
     * @param args
     * @return
     */
    abstract fun execute(args: List<String> = listOf(), data: MutableMap<String, Any?> = mutableMapOf()): Boolean

    /**
     * Get argument
     *
     * @param args
     * @param name
     * @param index
     * @param validator
     * @return
     */
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