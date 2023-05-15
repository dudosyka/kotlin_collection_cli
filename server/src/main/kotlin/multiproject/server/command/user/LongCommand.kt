package multiproject.server.command.user

import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.CommandSyncType
import multiproject.lib.udp.server.router.Controller

class LongCommand(controller: Controller) : Command(controller) {
    override val commandSyncType: CommandSyncType
        get() = CommandSyncType(true)
    /**
     * Execute
     *
     * @param input: ExecutableInput
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        println("Processing...")

        Thread.sleep(15_000)

        println("Processed!")

        return Response(ResponseCode.SUCCESS, "success processed!")
    }
}