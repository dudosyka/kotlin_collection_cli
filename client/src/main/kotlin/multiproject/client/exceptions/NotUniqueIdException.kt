package multiproject.client.exceptions

/**
 * Not unique id exception
 *
 * @constructor Create empty Not unique id exception
 */
class NotUniqueIdException: Exception() {
    override val message: String
        get() = "Error! Duplicate id"
}