package multiproject.server.file

import multiproject.server.command.CommandResolver
import multiproject.server.command.CommandResult
import multiproject.server.exceptions.RecursiveScriptException
import multiproject.server.io.Reader
import multiproject.server.io.IOData
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