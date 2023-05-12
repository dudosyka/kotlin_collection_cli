package multiproject.lib.exceptions

class InvalidSocketAddress: ExecuteException() {
    override val message: String
        get() = "Failed to interpret socket address from string"
}