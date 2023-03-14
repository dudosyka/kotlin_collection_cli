package lab5kotlin.io

abstract class Reader {
    abstract fun readLine(): String?
    abstract fun readCommand(): Any?
}