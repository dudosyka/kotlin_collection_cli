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
import multiproject.lib.dto.ConnectedServer
import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.request.RequestDto
import multiproject.client.exceptions.CommandNotFound
import multiproject.lib.exceptions.InvalidArgumentException
import multiproject.client.exceptions.RecursiveScriptException
import multiproject.lib.exceptions.ValidationFieldException
import multiproject.lib.udp.UdpConfig
import multiproject.lib.udp.interfaces.OnConnectionRefused
import multiproject.lib.udp.interfaces.OnConnectionRestored
import multiproject.lib.udp.client.runClient
import multiproject.lib.udp.interfaces.OnDisconnectAttempt
import multiproject.lib.udp.disconnect.RestoreOnDisconnectStrategy
import java.net.InetSocketAddress

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
                    defaultController = "collection"
                    onConnectionRestoredCallback = OnConnectionRestored {
                        response -> run {
                            CommandResolver.updateCommandList(response.commands)
                            println("Commands list updated from server!")
                        }
                    }
                    onConnectionRefusedCallback = OnConnectionRefused {
                        println("Callback. Connection refused")
                    }
                    onDisconnectAttempt = OnDisconnectAttempt {
                        attemptNum -> println("Reconnect attempt #$attemptNum")
                    }
                    addServer(
                        address = ConnectedServer(0, InetSocketAddress(UdpConfig.serverAddress, UdpConfig.serverPort))
                    )
                    disconnectStrategy = RestoreOnDisconnectStrategy()
                    bindOn(InetSocketAddress( "127.0.0.1", 7071))
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

    client.run()

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
                    client.sendRequest(RequestDto(PathDto(controller = "system",route = "_dump")))
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