package lab5kotlin.io

/**
 * Writer
 *
 * @constructor Create empty Writer
 */
abstract class Writer {
    /**
     * Write line
     *
     * @param line
     */
    abstract fun writeLine(line: String)

    /**
     * Write
     *
     * @param data
     */
    abstract fun write(data: String)
}