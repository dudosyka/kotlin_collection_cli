package multiproject.server.command.system

import multiproject.server.command.Command
import multiproject.lib.dto.command.CommandResult
import multiproject.server.command.CommandResolver

class SystemLoadCommand: Command() {
    override val hideFromClient: Boolean = true
    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        return CommandResult("", true, CommandResolver.getCommandsInfo())
    }
}