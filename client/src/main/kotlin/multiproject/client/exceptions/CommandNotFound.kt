package multiproject.client.exceptions

class CommandNotFound(val commandName: String): Exception() {
}