package lab5kotlin.io

abstract class Writer {
    abstract fun writeLine(line: String)

    abstract fun write(data: String)
}