package multiproject.server.command.user

import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller

class GetByTokenCommand(controller: Controller) : Command(controller) {
    override val description: String
        get() = "Returns current logged in user data"
    /**
     * Execute
     *
     * @param input: ExecutableInput
     * @return
     */
    override suspend fun execute(input: ExecutableInput): Response {
        return Response(ResponseCode.SUCCESS, input.data["user"].toString())
    }
}
