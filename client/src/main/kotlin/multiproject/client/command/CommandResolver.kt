package multiproject.client.command

import multiproject.client.exceptions.CommandNotFound
import multiproject.client.io.IOData
import multiproject.client.io.Writer
import multiproject.lib.udp.client.ClientUdpChannel
import multiproject.lib.dto.request.RequestDataDto
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.dto.command.CommandDto
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader
import multiproject.lib.dto.command.CommandResult
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.request.Request
import multiproject.lib.utils.LogLevel
import multiproject.lib.utils.Logger

/**
 * Command resolver
 *
 * @constructor Create empty Command resolver
 */
class CommandResolver {

    companion object {
        val logger: Logger by inject(Logger::class.java, named("logger"))
        val client: ClientUdpChannel by inject(ClientUdpChannel::class.java, named("client"))
        private val writer: Writer by inject(Writer::class.java, named("writer"))
        private var commands: Map<String, List<CommandDto>> = mapOf()

        fun getCommandByName(controller: String, name: String): CommandDto? {
            val commands = this.commands[controller]?.filter { it.name == name } ?: listOf()
            return if (commands.isNotEmpty())
                commands.first()
            else
                null
        }

        fun updateCommandList(commandList: Map<String, List<CommandDto>>) {
            logger(LogLevel.DEBUG, "$commandList")
            if (commandList.isNotEmpty())
                commands = commandList.map { controller -> controller.key to controller.value.filter { !it.hideFromClient }.filter { if (client.authorized) true else !it.needAuth } }.toMap()
            writer.writeLine("Commands list updated from server!")
            logger(LogLevel.DEBUG, "$commands")
        }

        fun loadCommands() {
            val response: ResponseDto = client.sendRequest(
                Request(PathDto("system", "_load"), data = RequestDataDto(mutableMapOf(), listOf()))
            )
            updateCommandList(response.commands)
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
        val route = split[0].split(".")
        var name = route[0]
        var controller = client.defaultController
        if (route.size > 1) {
            controller = route[0]
            name = route[1]
        }

        val args = split.subList(1, split.size)

        if (name == "exit")
            return CommandResult("exit")

        val command: CommandDto = getCommandByName(controller, name) ?: throw CommandNotFound(controller, name)

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


        val result = if (arguments.isNotEmpty()) {
            val objectData = ObjectBuilder(arguments).getEntityData()
             client.sendRequest(Request(PathDto(controller, name), data = RequestDataDto(objectData, inlineData)))
        } else {
            client.sendRequest(Request(PathDto(controller, name), data = RequestDataDto(mutableMapOf(),  inlineData)))
        }

        if (command.authorizedEndpoint && result.code.toString() == "SUCCESS") {
            client.auth(result.result)
            loadCommands()
            return CommandResult("Command resolved", true, ResponseDto(ResponseCode.SUCCESS, "Successfully authorized!"))
        }

        return CommandResult("Command resolved", true, result)
    }
}
