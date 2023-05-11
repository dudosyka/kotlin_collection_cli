package multiproject.server.command.user

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
    override fun execute(input: ExecutableInput): Response {
        println("Processing...")
//        var processing = true

//        val reconnectTask: TimerTask = object: TimerTask() {
//            override fun run() {
//                println("processed!")
//                processing = false
//            }
//        }
//        Timer().schedule(
//            reconnectTask, UdpConfig.timeout / 2
//        )

//        while (processing) { }

        Thread.sleep(15_000)

        println("truly processed!")

        return Response(ResponseCode.SUCCESS, "success processed!")
    }
}