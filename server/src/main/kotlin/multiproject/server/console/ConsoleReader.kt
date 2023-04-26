package multiproject.server.console

import multiproject.server.command.CommandResolver
import multiproject.server.command.CommandResult
import multiproject.server.io.Reader
import multiproject.server.io.IOData

/**
 * Console reader
 *
 * @constructor Create empty Console reader
 */
class ConsoleReader : Reader() {
    override fun readLine(): String? {
        return readlnOrNull()
    }
    override fun readCommand(): CommandResult? {
        val line = this.readLine() ?: return null

        IOData.current = "console"

        val resolver = CommandResolver()
        return resolver.handle(line)
    }
}