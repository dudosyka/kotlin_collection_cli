package multiproject.client.command

import multiproject.lib.exceptions.CommandNotFound
import multiproject.client.io.IOData
import multiproject.lib.udp.ClientUdpChannel
import multiproject.lib.dto.RequestDataDto
import multiproject.lib.dto.RequestDto
import multiproject.lib.dto.ResponseDto
import multiproject.lib.dto.command.CommandDto
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader

/**
 * Command resolver
 *
 * @constructor Create empty Command resolver
 */
class CommandResolver {

    companion object {
        val client: ClientUdpChannel by inject(ClientUdpChannel::class.java, named("client"))
        var commands: List<CommandDto> = listOf()

        fun getCommandByName(name: String): CommandDto? {
            val commands = this.commands.filter { it.name == name }
            return if (commands.isNotEmpty())
                commands.first()
            else
                null
        }

        fun loadCommands() {
            val response: ResponseDto = client.sendRequest(RequestDto("load", RequestDataDto(mapOf(), listOf())))
            commands = response.commands
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
        val command: CommandDto = getCommandByName(name) ?: throw CommandNotFound(name)

        val inlineData: List<Any> = InlineArgumentsValidator(
            args,
            command.arguments.filter { it.value.inline }
        ).getArguments() ?: return CommandResult("Inline arguments validation failed!", false)

        //If we get command which used to switch between sources - switch
        if (command.fileReaderSource) {
            return try {
                val inputStream = FileInputStream(inlineData[0] as String)
                val fileReader = BufferedReader(InputStreamReader(inputStream))
                IOData.current = "file"
                IOData.fileReader = fileReader
                IOData.changeSourceCommand = command.name
                if (IOData.commandHistory.isEmpty()) {
                    IOData.commandHistory = mutableListOf(
                        "execute_script ${inlineData[0]}"
                    )
                }
                CommandResult("")
            } catch (e: FileNotFoundException) {
                CommandResult("Error! ${e.message}", false)
            } catch (e: Exception) {
                CommandResult("Failed switch source", false)
            }
        }

        val arguments = command.arguments.filter { !it.value.inline }

        if (arguments.isNotEmpty()) {
            val objectData = ObjectBuilder(arguments).getEntityData()
            println(objectData)
            return CommandResult("Command resolved", true, client.sendRequest(RequestDto(name, RequestDataDto(objectData, inlineData))))
        }

        return CommandResult("Command resolved", true, client.sendRequest(RequestDto(name, RequestDataDto(mapOf(),  inlineData))))
    }
}
