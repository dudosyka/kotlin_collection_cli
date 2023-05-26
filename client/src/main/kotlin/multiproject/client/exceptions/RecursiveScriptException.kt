package multiproject.client.exceptions

import multiproject.lib.exceptions.client.ClientExecutionException

/**
 * Recursive script exception
 *
 * @constructor Create empty Recursive script exception
 */
class RecursiveScriptException: ClientExecutionException() {
    override val message: String
        get() = "Error! Recursive script cached!"
}