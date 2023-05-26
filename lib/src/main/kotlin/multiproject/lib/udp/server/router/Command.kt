package multiproject.lib.udp.server.router

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.command.Validator
import multiproject.lib.dto.response.Response
import multiproject.lib.exceptions.ValidationFieldException

/**
 * Command
 *
 * @constructor Create empty Command
 */
abstract class Command(val controller: Controller) {

    open val needObject = false
    open val fields: Map<String, CommandArgumentDto> = mapOf()
    open val fileReaderSource = false
    open val description = "no description."
    open val hideFromClient = false
    open val commandSyncType: CommandSyncType = CommandSyncType(sync = false, blocking = false)
    open val needAnswer = true
    /**
     * Execute
     *
     * @param input: ExecutableInput
     * @return
     */
    abstract suspend fun execute(input: ExecutableInput): Response

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
            throw ValidationFieldException(validator)
        }
        val argumentValue = args[index] ?: throw ValidationFieldException(validator)
        if (!validator.validate(argumentValue))
            throw ValidationFieldException(validator)
        return validator.value!!
    }

    fun getHelpString(): String {
        val args = fields.filter { it.value.inline }.map { "{${it.key}}" }.joinToString(" ")
        return "$args - $description"
    }
}