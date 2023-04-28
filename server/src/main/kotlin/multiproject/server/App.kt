/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package multiproject.server

import multiproject.server.collection.item.EntityBuilder
import multiproject.server.collection.Collection
import multiproject.server.command.CommandResolver
import multiproject.server.dump.DumpManager
import multiproject.server.dump.FileDumpManager
import multiproject.server.entities.flat.Flat
import multiproject.server.entities.flat.FlatBuilder
import multiproject.server.entities.flat.FlatCollection
import multiproject.lib.udp.ServerUdpChannel
import multiproject.lib.dto.ResponseCode
import multiproject.lib.dto.ResponseDto
import multiproject.lib.dto.Serializer
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.ByteBuffer

class App (filePath: String) {
    init {
        val module = module {
            single<Collection<Flat>>(named("collection")) {
                FlatCollection(mutableListOf())
            }
            single<DumpManager<Flat>>(named("dumpManager")) {
                FileDumpManager(filePath, Flat.serializer())
            }
            single<EntityBuilder<Flat>>(named("builder")) {
                FlatBuilder()
            }
        }
        startKoin {
            modules(
                module
            )
        }
    }
}


fun main() {
    App("/Users/dudosyka/IdeaProjects/lab5Kotlin/data.csv")
    val server = ServerUdpChannel(
        onReceive = {
            channel, address, data -> run {
                println(data)
                channel.send(
                    ByteBuffer.wrap(
                        Serializer.serializeResponse(
                            CommandResolver.run( data.command, data.data?.inlineArguments, data.data?.arguments )
                        ).toByteArray()
                    ),
                    address
                )
            }
        },
        onFirstConnect = {
            channel, address -> run {
                println("First connect of $address")
                val commandList = CommandResolver.getCommandsInfo()
                val response = ResponseDto(
                    code = ResponseCode.SUCCESS,
                    result = "",
                    commands = commandList
                )
                channel.send(ByteBuffer.wrap(Serializer.serializeResponse(response).toByteArray()), address)
            }
        }
    )
    server.run()
}
