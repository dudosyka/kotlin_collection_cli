package lab5kotlin.file

import lab5kotlin.command.CommandResolver
import lab5kotlin.exceptions.RecursiveScriptException
import lab5kotlin.io.Reader
import lab5kotlin.io.IOData
import lab5kotlin.io.Writer
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import java.io.BufferedReader

/**
 * File reader
 *
 * @property fileReader
 * @property commandHistory
 * @constructor Create empty File reader
 */
class FileReader(private val fileReader: BufferedReader?, private val commandHistory: MutableList<String>): Reader() {
    private val writer: Writer by KoinJavaComponent.inject(Writer::class.java, named("writer"))
    override fun readLine(): String? {
        return this.fileReader!!.readLine()
    }

    override fun readCommand(): Any? {
        val line = this.readLine()

        if (line == null) {
            IOData.current = "console"
            this.writer.writeLine("File execution finished!")
            return true
        }

        if (line.split(" ")[0] == "execute_script" && commandHistory.contains(line))
            throw RecursiveScriptException()

        commandHistory.add(line)

        val resolver = CommandResolver()
        return resolver.handle(line)
    }
}