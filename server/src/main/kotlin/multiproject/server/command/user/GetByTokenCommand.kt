package multiproject.server.command.user

import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller

class GetByTokenCommand(controller: Controller) : Command(controller) {
    /**
     * Execute
     *
     * @param input: ExecutableInput
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        return Response(ResponseCode.SUCCESS, input.data["user"].toString())
    }
}
