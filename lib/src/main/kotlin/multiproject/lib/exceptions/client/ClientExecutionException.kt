package multiproject.lib.exceptions.client

abstract class ClientExecutionException: Exception() {
    abstract override val message: String
}