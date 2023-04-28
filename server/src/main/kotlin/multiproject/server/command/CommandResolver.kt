package multiproject.server.command

import multiproject.lib.exceptions.*
import multiproject.lib.dto.ResponseCode
import multiproject.lib.dto.ResponseDto
import multiproject.lib.dto.command.CommandDto
import multiproject.lib.dto.command.CommandResult
import multiproject.server.command.system.SystemDumpCommand
import multiproject.server.command.system.SystemLoadCommand
import java.net.SocketAddress

/**
 * Command resolver
 *
 * @constructor Create empty Command resolver
 */
class CommandResolver {
    companion object {
        var author: SocketAddress? = null

        val commands: Map<String, Command> = mapOf(
            "help" to HelpCommand(),
            "info" to InfoCommand(),
            "add" to AddCommand(),
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
            "_load" to SystemLoadCommand(),
            "_dump" to SystemDumpCommand()
        )

        fun getCommandsInfo(): ResponseDto {
            return ResponseDto(
                code = ResponseCode.SUCCESS,
                result = "",
                commands = commands.map {
                    CommandDto(
                        name = it.key,
                        arguments = it.value.fields,
                        hideFromClient = it.value.hideFromClient,
                        fileReaderSource = it.value.fileReaderSource
                    )
                }
            )
        }

        fun run(name: String, inline: List<Any?>?, args: Map<String, Any?>?): ResponseDto {
            val command = this.commands[name] ?: return ResponseDto(code = ResponseCode.NOT_FOUND, "Command not found!")
            return try {
                val result: CommandResult = command.execute(inline ?: listOf(), (args ?: mapOf()).toMutableMap()) ?: return ResponseDto(code = ResponseCode.INTERNAL_SERVER_ERROR, "Failed command process")
                if (command.hideFromClient)
                    return result.responseDto!!
                ResponseDto(code = ResponseCode.SUCCESS, result = result.body)
            } catch (e: InvalidArgumentException) {
                ResponseDto(code = ResponseCode.VALIDATION_ERROR, result = e.validationRulesDescribe)
            } catch (e: ItemNotFoundException) {
                ResponseDto(code = ResponseCode.ITEM_NOT_FOUND, result = e.message)
            } catch (e: ValidationFieldException) {
                ResponseDto(code = ResponseCode.VALIDATION_ERROR, result = e.message)
            } catch (e: Exception) {
                ResponseDto(code = ResponseCode.INTERNAL_SERVER_ERROR, result = e.message ?: "error!")
            }
        }
    }
}
