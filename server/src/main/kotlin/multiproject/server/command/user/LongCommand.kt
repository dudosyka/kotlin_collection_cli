package multiproject.server.command.user

import kotlinx.coroutines.delay
import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller

class LongCommand(controller: Controller) : Command(controller) {
    /**
     * Execute
     *
     * @param input: ExecutableInput
     * @return
     */
    override suspend fun execute(input: ExecutableInput): Response {
        println("Processing...")

        delay(15_000)

        println("Processed!")

        return Response(ResponseCode.SUCCESS, "success processed!")
    }
}