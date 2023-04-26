package multiproject.client.command

import multiproject.udpsocket.ClientUdpChannel
import multiproject.udpsocket.dto.RequestDataDto
import multiproject.udpsocket.dto.RequestDto
import multiproject.udpsocket.dto.command.CommandDto
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Command resolver
 *
 * @constructor Create empty Command resolver
 */
class CommandResolver {
    val client: ClientUdpChannel by inject(ClientUdpChannel::class.java, named("client"))

    companion object {
        var commands: List<CommandDto> = listOf()

        fun getCommandByName(name: String): CommandDto? {
            val commands = this.commands.filter { it.name == name }
            return if (commands.isNotEmpty())
                commands.first()
            else
                null
        }
    }
    /**
     * Handle
     *
     * @param commandLine
     * @return
     */
    fun handle(commandLine: String): CommandResult {
        val split = commandLine.split(" ")
        val name = split[0]
        val args = split.subList(1, split.size)
        val command: CommandDto = getCommandByName(name) ?: return CommandResult("Command not found!", false)
        val inlineData: List<Any> = InlineArgumentsValidator(args, command.arguments.filter { it.value.inline }).getArguments() ?: return CommandResult("Inline arguments validation failed!", false)
        val arguments = command.arguments.filter { !it.value.inline }
        if (arguments.isNotEmpty()) {
            val objectData = ObjectBuilder(arguments).getEntityData()
            println(objectData)
            return CommandResult("Command resolved", true, client.sendRequest(RequestDto(name, RequestDataDto(objectData, inlineData))))
        }
        return CommandResult("Command resolved", true, client.sendRequest(RequestDto(name, RequestDataDto(mapOf(),  inlineData))))
    }
}
