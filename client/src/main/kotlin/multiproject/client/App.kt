package multiproject.client

import multiproject.client.command.CommandResolver
import multiproject.client.console.ConsoleReader
import multiproject.client.console.ConsoleWriter
import multiproject.client.exceptions.InvalidArgumentException
import multiproject.client.exceptions.ItemNotFoundException
import multiproject.client.exceptions.RecursiveScriptException
import multiproject.client.exceptions.ValidationFieldException
import multiproject.client.io.IOData
import multiproject.client.io.Reader
import multiproject.client.io.Writer
import multiproject.udpsocket.ClientUdpChannel
import multiproject.udpsocket.dto.RequestDto
import multiproject.udpsocket.dto.command.FieldType
import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import multiproject.client.file.FileReader
import multiproject.udpsocket.dto.RequestDataDto
import multiproject.udpsocket.dto.ResponseDto

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
                ClientUdpChannel()
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
    val response: ResponseDto = client.sendRequest(RequestDto("load", RequestDataDto(mapOf(), listOf())))
    CommandResolver.commands = response.commands

    var readNextLine = true
    while (readNextLine) {
        val reader: Reader by inject(Reader::class.java, named("reader"))
        try {
            val requestDto = reader.readCommand()

            if (requestDto == null)
                readNextLine = false
            else
                requestDto.responseDto?.result?.let { writer.writeLine(it) }

        } catch (e: InvalidArgumentException) {
            writer.writeLine(e.validationRulesDescribe)
        } catch (e: ItemNotFoundException) {
            writer.writeLine("Error! ${e.message}")
        } catch (e: ValidationFieldException) {
            writer.writeLine(e.message)
        } catch (e: RecursiveScriptException) {
            writer.writeLine("Error recursive script!")
        } catch (e: Exception) {
            writer.writeLine(e.stackTraceToString())
            writer.writeLine(e.toString())
        }
    }
}