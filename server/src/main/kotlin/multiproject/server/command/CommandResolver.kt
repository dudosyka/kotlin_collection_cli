package multiproject.server.command

import multiproject.udpsocket.dto.ResponseCode
import multiproject.udpsocket.dto.ResponseDto
import multiproject.udpsocket.dto.command.CommandDto

/**
 * Command resolver
 *
 * @constructor Create empty Command resolver
 */
class CommandResolver {
    companion object {
        val commands: Map<String, Command> = mapOf(
            "help" to HelpCommand(),
            "info" to InfoCommand(),
//            "add" to AddCommand(),
            "show" to ShowCommand(),
            "update" to UpdateCommand(),
            "remove_by_id" to RemoveByIdCommand(),
            "clear" to ClearCommand(),
            "save" to SaveCommand(),
            "load" to LoadCommand(),
            "execute_script" to ExecuteScriptCommand(),
            "remove_at" to RemoveAtCommand(),
            "add_if_max" to AddIfMaxCommand(),
            "reorder" to ReorderCommand(),
            "count_by_number_of_rooms" to CountByNumberOfRoomsCommand(),
            "count_less_than_time_to_metro_by_transport" to CountLessThanTimeToMetroByTransportCommand(),
            "filter_less_than_furnish" to FilterLessThanFurnish(),
        )

        fun getCommandsInfo(): List<CommandDto> {
            return commands.map {
                CommandDto(
                    name = it.key,
                    arguments = it.value.fields,
                    fileReaderSource = it.value.fileReaderSource
                )
            }
        }

        fun run(name: String, inline: List<Any?>?, args: Map<String, Any?>?): ResponseDto {
            val command = this.commands[name] ?: return ResponseDto(code = ResponseCode.NOT_FOUND, "Command not found!")
            val result = command.execute(inline ?: listOf(), (args ?: mapOf()).toMutableMap()) ?: return ResponseDto(code = ResponseCode.INTERNAL_SERVER_ERROR, "Failed command process")
            return ResponseDto(code = ResponseCode.SUCCESS, result = result.body)
        }
    }
}
