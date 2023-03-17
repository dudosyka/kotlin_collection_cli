package lab5kotlin.file

import lab5kotlin.command.CommandResolver
import lab5kotlin.command.CommandResult
import lab5kotlin.exceptions.RecursiveScriptException
import lab5kotlin.io.Reader
import lab5kotlin.io.IOData
import java.io.BufferedReader

/**
 * File reader
 *
 * @property fileReader
 * @property commandHistory
 * @constructor Create empty File reader
 */
class FileReader(private val fileReader: BufferedReader?, private val commandHistory: MutableList<String>): Reader() {
    override fun readLine(): String? {
        return this.fileReader!!.readLine()
    }

    override fun readCommand(): CommandResult? {
        val line = this.readLine()

        if (line == null) {
            IOData.current = "console"
            IOData.commandHistory = mutableListOf()
            IOData.fileReader = null
            return CommandResult("File execution finished!")
        }

        if (line.split(" ")[0] == "execute_script" && commandHistory.contains(line))
            throw RecursiveScriptException()

        commandHistory.add(line)

        val resolver = CommandResolver()
        return resolver.handle(line)
    }
}