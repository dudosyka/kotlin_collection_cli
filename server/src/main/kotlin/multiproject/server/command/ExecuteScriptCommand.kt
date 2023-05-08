package multiproject.server.command

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.lib.utils.ExecutableInput

/**
 * Execute script command
 *
 * @constructor Create empty Execute script command
 */
class ExecuteScriptCommand(controller: Controller) : Command(controller) {
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
    override fun execute(input: ExecutableInput): Response {
        return Response(ResponseDto(ResponseCode.SUCCESS, ""))
    }
}