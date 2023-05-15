package multiproject.server.command.system

import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.CommandSyncType
import multiproject.lib.udp.server.router.Controller

class SyncCommand(controller: Controller) : Command(controller) {
    override val commandSyncType: CommandSyncType
        get() = CommandSyncType(sync = true)
    /**
     * Execute
     *
     * @param input: ExecutableInput
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        println("Processing...")

        Thread.sleep(10_000)

        println("Processed!")
        return Response(ResponseCode.SUCCESS, "Synchronized!")
    }
}