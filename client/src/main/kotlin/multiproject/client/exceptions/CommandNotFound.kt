package multiproject.client.exceptions

import multiproject.lib.exceptions.client.ClientExecutionException

class CommandNotFound(private val controller: String, private val commandName: String): ClientExecutionException() {
    override val message: String
        get() = "Command with name $commandName not found in $controller controller!"
}