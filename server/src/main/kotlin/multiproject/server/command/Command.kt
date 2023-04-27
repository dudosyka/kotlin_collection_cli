package multiproject.server.command

import multiproject.server.collection.item.Validator
import multiproject.server.exceptions.InvalidArgumentException
import multiproject.udpsocket.dto.command.CommandArgumentDto

/**
 * Command
 *
 * @constructor Create empty Command
 */
abstract class Command {
    open val needObject = false
    open val fields: Map<String, CommandArgumentDto> = mapOf()
    open val fileReaderSource = false
    open val description = "no description."
    /**
     * Execute
     *
     * @param args
     * @return
     */
    abstract fun execute(args: List<Any?> = listOf(), data: MutableMap<String, Any?> = mutableMapOf()): CommandResult?

    /**
     * Get argument
     *
     * @param args
     * @param name
     * @param index
     * @param validator
     * @return
     */
    fun getArgument(args: List<Any?>, name: String, index: Int, validator: Validator): Any {
        if (args.size < index || args.isEmpty() || index < 0) {
            throw InvalidArgumentException(name, validator.describe())
        }
        val argumentValue = args[index] ?: throw InvalidArgumentException(name, validator.describe())
        if (!validator.validate(argumentValue))
            throw InvalidArgumentException(name, validator.describe())
        return validator.value!!
    }

    fun getHelpString(): String {
        val args = fields.filter { it.value.inline }.map { "{${it.key}}" }.joinToString(" ")
        return "$args - $description"
    }
}