package lab5kotlin.command

/**
 * Exit command
 *
 * @constructor Create empty Exit command
 */
class ExitCommand : Command() {
    override fun execute(args: List<String>, data: MutableMap<String, Any?>): Boolean {
        return false
    }
}