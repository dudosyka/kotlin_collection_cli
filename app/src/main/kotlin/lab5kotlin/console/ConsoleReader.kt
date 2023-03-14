package lab5kotlin.console

import lab5kotlin.command.CommandResolver
import lab5kotlin.io.Reader
import lab5kotlin.io.IOData

/**
 * Console reader
 *
 * @constructor Create empty Console reader
 */
class ConsoleReader : Reader() {
    override fun readLine(): String? {
        return readlnOrNull()
    }
    override fun readCommand(): Any? {
        val line = this.readLine() ?: return null

        IOData.current = "console"

        val resolver = CommandResolver()
        return resolver.handle(line)
    }
}