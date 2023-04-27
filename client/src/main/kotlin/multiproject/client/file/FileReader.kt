package multiproject.client.file

import multiproject.client.command.CommandResolver
import multiproject.client.command.CommandResult
import multiproject.client.exceptions.RecursiveScriptException
import multiproject.client.io.Reader
import multiproject.client.io.IOData
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

    override fun readCommand(): CommandResult {
        val line = this.readLine()

        if (line == null) {
            IOData.current = "console"
            IOData.commandHistory = mutableListOf()
            IOData.fileReader = null
            return CommandResult("File execution finished!")
        }

        if (line.split(" ")[0] == IOData.changeSourceCommand && commandHistory.contains(line))
            throw RecursiveScriptException()

        commandHistory.add(line)

        val resolver = CommandResolver()
        return resolver.handle(line)
    }
}