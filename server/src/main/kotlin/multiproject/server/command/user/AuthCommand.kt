package multiproject.server.command.user

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.ServerUdpChannel
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.server.modules.auth.Auth
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class AuthCommand(controller: Controller) : Command(controller) {
    val server: ServerUdpChannel by KoinJavaComponent.inject(ServerUdpChannel::class.java, named("server"))
    override val fields: Map<String, CommandArgumentDto> = mapOf(
        "login" to CommandArgumentDto(
            name = "login",
            required = true,
            type = multiproject.lib.dto.command.FieldType.STRING
        ),
        "password" to CommandArgumentDto(
            name = "password",
            required = true,
            type = multiproject.lib.dto.command.FieldType.STRING
        ),
    )
    /**
     * Execute
     *
     * @param input: ExecutableInput
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        val authModule = Auth(input.data["login"]?.toString() ?: "", input.data["password"]?.toString() ?: "")
        return Response(ResponseCode.SUCCESS, authModule.login(), commands = server.router.getCommandsInfo())
    }
}
