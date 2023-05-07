package multiproject.client

import multiproject.client.command.CommandResolver
import multiproject.client.console.ConsoleReader
import multiproject.client.console.ConsoleWriter
import multiproject.client.io.IOData
import multiproject.client.io.Reader
import multiproject.client.io.Writer
import multiproject.lib.udp.client.ClientUdpChannel
import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import multiproject.client.file.FileReader
import multiproject.lib.dto.RequestDto
import multiproject.lib.exceptions.CommandNotFound
import multiproject.lib.exceptions.InvalidArgumentException
import multiproject.lib.exceptions.RecursiveScriptException
import multiproject.lib.exceptions.ValidationFieldException
import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.client.runClient

class App {
    init {
        val module = module {
            factory<Reader>(named("reader")) {
                if (IOData.current == "file")
                    FileReader(IOData.fileReader, IOData.commandHistory)
                else
                    ConsoleReader()
            }
            single<Writer>(named("writer")) {
                ConsoleWriter()
            }
            single<ClientUdpChannel>(named("client")) {
                runClient {
                    onConnectionRestored {
                        response -> run {
                            CommandResolver.updateCommandList(response.commands)
                            println("Commands list updated from server!")
                        }
                    }
                    onConnectionRefused {
                        println("Connection lost! Try to reconnect!")
                    }
                    setServer(
                        serverAddress = UdpConfig.serverAddress,
                        serverPort = UdpConfig.serverPort
                    )
                }
            }
        }
        GlobalContext.startKoin {
            modules(
                module
            )
        }
    }
}

fun main() {
    App()
    val writer: Writer = ConsoleWriter()
    writer.writeLine("^_^ Welcome to the Collection CLI ^_^")

    val client: ClientUdpChannel by inject(ClientUdpChannel::class.java, named("client"))

    client.connect()

    CommandResolver.loadCommands()

    var readNextLine = true
    while (readNextLine) {
        val reader: Reader by inject(Reader::class.java, named("reader"))
        try {
            val result = reader.readCommand()

            if (result == null)
                readNextLine = false
            else if (result.responseDto != null) {
                if (result.responseDto!!.commands.isNotEmpty()) {
                    CommandResolver.updateCommandList(result.responseDto!!.commands)
                    writer.writeLine("Commands list updated from server!")
                } else {
                    writer.writeLine(result.responseDto!!.result)
                }

            }
            else {
                if (result.body == "exit") {
                    readNextLine = false
                    client.sendRequest(RequestDto("_dump"))
                    writer.writeLine("Application stopping...")
                    client.stop()
                } else {
                    writer.writeLine(result.body)
                }
            }

        } catch (e: InvalidArgumentException) {
            writer.writeLine(e.validationRulesDescribe)
        } catch (e: ValidationFieldException) {
            writer.writeLine(e.message)
        } catch (e: RecursiveScriptException) {
            writer.writeLine("Error recursive script!")
        } catch (e: CommandNotFound) {
            writer.writeLine("Command not found. (Try to synchronize...)")
            CommandResolver.loadCommands()
        } catch (e: Exception) {
            writer.writeLine(e.stackTraceToString())
            writer.writeLine(e.toString())
        }
    }
}