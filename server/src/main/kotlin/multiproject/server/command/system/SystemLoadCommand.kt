package multiproject.server.command.system

import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.ServerUdpChannel
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class SystemLoadCommand(controller: Controller) : Command(controller) {
    override val hideFromClient: Boolean = true
    val server: ServerUdpChannel by inject(ServerUdpChannel::class.java, named("server"))
    /**
     * Execute
     *
     * @param input
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        return Response(ResponseCode.SUCCESS, "", commands = server.router.getCommandsInfo())
    }
}