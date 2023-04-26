package multiproject.server.command

/**
 * Exit command
 *
 * @constructor Create empty Exit command
 */
class ExitCommand : Command() {
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult? {
        return null
    }
}