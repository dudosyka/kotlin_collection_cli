package lab5kotlin.io

/**
 * Reader
 *
 * @constructor Create empty Reader
 */
abstract class Reader {
    /**
     * Read line
     *
     * @return
     */
    abstract fun readLine(): String?

    /**
     * Read command
     *
     * @return
     */
    abstract fun readCommand(): Any?
}