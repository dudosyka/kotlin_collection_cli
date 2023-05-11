package multiproject.lib.exceptions

import multiproject.lib.utils.ExecuteException

class InvalidSocketAddress: ExecuteException() {
    override val message: String
        get() = "Failed to interpret socket address from string"
}