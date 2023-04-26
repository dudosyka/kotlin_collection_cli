package multiproject.server.command

import multiproject.udpsocket.dto.ResponseCode
import multiproject.udpsocket.dto.ResponseDto
import multiproject.udpsocket.dto.command.CommandArgumentDto
import multiproject.udpsocket.dto.command.CommandDto

/**
 * Command resolver
 *
 * @constructor Create empty Command resolver
 */
class CommandResolver {
    companion object {
        private val commands: Map<String, Command> = mapOf(
            "help" to HelpCommand(),
            "info" to InfoCommand(),
            "add" to AddCommand(),
            "show" to ShowCommand(),
//        "update" to UpdateCommand(),
//        "remove_by_id" to RemoveByIdCommand(),
//        "clear" to ClearCommand(),
//        "save" to SaveCommand(),
//        "load" to LoadCommand(),
//        "execute_script" to ExecuteScriptCommand(),
//        "exit" to ExitCommand(),
//        "remove_at" to RemoveAtCommand(),
//        "add_if_max" to AddIfMaxCommand(),
//        "reorder" to ReorderCommand(),
//        "count_by_number_of_rooms" to CountByNumberOfRoomsCommand(),
//        "count_less_than_time_to_metro_by_transport" to CountLessThanTimeToMetroByTransportCommand(),
//        "filter_less_than_furnish" to FilterLessThanFurnish(),
        )

        fun getCommandsInfo(): List<CommandDto> {
            return commands.map { CommandDto(name=it.key, arguments = it.value.fields) }
        }

        fun run(name: String, inline: List<Any?>?, args: Map<String, Any?>?): ResponseDto {
            val command = this.commands[name] ?: return ResponseDto(code = ResponseCode.NOT_FOUND, "Command not found!")
            val result = command.execute(inline ?: listOf(), (args ?: mapOf()).toMutableMap()) ?: return ResponseDto(code = ResponseCode.INTERNAL_SERVER_ERROR, "Failed command process")
            return ResponseDto(code = ResponseCode.SUCCESS, result = result.body)
        }
    }

    /**
     * Handle
     *
     * @param commandLine
     * @return
     */
    fun handle(commandLine: String): CommandResult? {
        val split = commandLine.split(" ")
        val name = split[0]
        val args = split.subList(1, split.size)
        val command: Command = commands[name] ?: return CommandResult("Command not found!",false)
        if (command.needObject) {
            val builder = ObjectBuilder(command.fields)
            return command.execute(args, builder.getEntityData())
        }
        return command.execute(args)
    }
}
