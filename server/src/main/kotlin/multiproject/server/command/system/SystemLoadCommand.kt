package multiproject.server.command.system

import multiproject.lib.udp.server.router.Command
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.udp.server.ServerUdpChannel
import multiproject.lib.udp.server.router.Controller
import multiproject.lib.utils.ExecutableInput
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class SystemLoadCommand(controller: Controller) : Command(controller) {
    override val hideFromClient: Boolean = true
    val server: ServerUdpChannel by inject(ServerUdpChannel::class.java, named("server"))
    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        return Response(ResponseDto(ResponseCode.SUCCESS, "", commands = server.router.getCommandsInfo("collection")))
    }
}