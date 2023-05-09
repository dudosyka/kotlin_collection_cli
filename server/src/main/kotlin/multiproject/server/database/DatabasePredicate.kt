package multiproject.server.database

data class DatabasePredicate (
    val column: String,
    val op: String,
    val value: String
)