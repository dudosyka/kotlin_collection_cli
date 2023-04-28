package multiproject.lib.exceptions

/**
 * Item not found exception
 *
 * @property searchField
 * @property searchValue
 * @constructor Create empty Item not found exception
 */
class ItemNotFoundException(private val searchField: String, private val searchValue: Any): Exception() {
    override val message: String
        get() = "Item with $searchField = $searchValue not found!"
}