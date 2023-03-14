package lab5kotlin.console

import lab5kotlin.io.Writer

/**
 * Console writer
 *
 * @constructor Create empty Console writer
 */
class ConsoleWriter: Writer() {
    override fun writeLine(line: String) {
        println(line)
    }

    override fun write(data: String) {
        print(data)
    }
}