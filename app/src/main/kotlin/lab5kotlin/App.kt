/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package lab5kotlin

import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.console.ConsoleReader
import lab5kotlin.console.ConsoleWriter
import lab5kotlin.dump.DumpManager
import lab5kotlin.dump.FileDumpManager
import lab5kotlin.exceptions.*
import lab5kotlin.file.FileReader
import lab5kotlin.human.Human
import lab5kotlin.human.HumanBuilder
import lab5kotlin.human.HumanCollection
import lab5kotlin.io.Reader
import lab5kotlin.io.IOData
import lab5kotlin.io.Writer
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject

/**
 * App
 *
 * @constructor
 *
 * @param filePath
 */
class App (filePath: String) {
    init {
        val module = module {
            single<Collection<Human>>(named("collection")) {
                HumanCollection(mutableListOf())
            }
            single<DumpManager<Human>>(named("dumpManager")) {
                FileDumpManager(filePath, Human.serializer())
            }
            factory<Reader>(named("reader")) {
                if (IOData.current == "file")
                    FileReader(IOData.fileReader, IOData.commandHistory)
                else
                    ConsoleReader()
            }
            single<Writer>(named("writer")) {
                ConsoleWriter()
            }
            single<EntityBuilder<Human>>(named("builder")) {
                HumanBuilder()
            }
        }
        startKoin {
            modules(
                module
            )
        }
    }
}

/**
 * Main
 *
 * @param args
 */
fun main(args: Array<String>) {
    val writer: Writer = ConsoleWriter()
    if (args.isEmpty()) {
        writer.write("Error! You must pass filePath in arguments.")
        return
    }

    App(args[0])
    val collection: Collection<Human> by inject(Collection::class.java, named("collection"))

    try {
        collection.loadDump()
    } catch (e: FileDumpException) {
        writer.writeLine(e.parent.toString())
        writer.writeLine(e.parent.message!!)
        writer.writeLine(e.message)
    } catch (e: NotUniqueIdException) {
        writer.writeLine(e.message)
    }

    var readNextLine = true
    var i = 0
    while (readNextLine && i < 10) {
        i++
        val reader: Reader by inject(Reader::class.java, named("reader"))
        try {
            val commandResult = reader.readCommand()

            if (commandResult == false)
                readNextLine = false

            if (commandResult == null)
                println("Command not found!")

        } catch (e: InvalidArgumentException) {
            writer.writeLine(e.validationRulesDescribe)
        } catch (e: ItemNotFoundException) {
            writer.writeLine("Error! ${e.message}")
        } catch (e: ValidationFieldException) {
            writer.writeLine(e.message)
        } catch (e: Exception) {
            writer.writeLine(e.stackTraceToString())
            writer.writeLine(e.toString())
            readNextLine = false

        }
    }
}
