package lab5kotlin.exceptions

class ItemNotFoundException(private val searchField: String, private val searchValue: Any): Exception() {
    override val message: String
        get() = "Item with $searchField = $searchValue not found!"
}