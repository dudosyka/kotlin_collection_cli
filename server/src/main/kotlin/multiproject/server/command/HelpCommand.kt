package multiproject.server.command

import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.udp.server.ServerUdpChannel
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.lib.utils.ExecutableInput
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Help command
 *
 * @constructor Create empty Help command
 */
class HelpCommand(controller: Controller) : Command(controller) {
    override val description: String = "Show help text"

    val server: ServerUdpChannel by inject(ServerUdpChannel::class.java, named("server"))
    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        val commands = server.router.getRoutes(controller.name).filter {
            !it.command.hideFromClient
        }
        return Response(ResponseDto(
            ResponseCode.SUCCESS, commands.joinToString("\n") { "${it.name} ${it.command.getHelpString()}" }
        ))
    }
}