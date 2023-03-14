package lab5kotlin.console

import lab5kotlin.io.Writer

class ConsoleWriter: Writer() {
    override fun writeLine(line: String) {
        println(line)
    }

    override fun write(data: String) {
        print(data)
    }
}