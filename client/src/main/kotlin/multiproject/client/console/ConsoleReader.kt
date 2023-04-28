package multiproject.client.console

import multiproject.client.command.CommandResolver
import multiproject.lib.dto.command.CommandResult
import multiproject.client.io.Reader
import multiproject.client.io.IOData

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