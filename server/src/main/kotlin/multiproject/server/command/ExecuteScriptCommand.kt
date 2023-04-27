package multiproject.server.command

import multiproject.udpsocket.dto.command.CommandArgumentDto
import multiproject.udpsocket.dto.command.FieldType

/**
 * Execute script command
 *
 * @constructor Create empty Execute script command
 */
class ExecuteScriptCommand: Command() {
    override val fields = mapOf(
        "path" to CommandArgumentDto(
            name = "path",
            inline = true,
            index = 0,
            type = FieldType.STRING
        )
    )
    override val fileReaderSource: Boolean = true
    override val description: String = "Run script"
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        return CommandResult("")
    }
}