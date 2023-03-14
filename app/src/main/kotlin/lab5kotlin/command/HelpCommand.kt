package lab5kotlin.command

/**
 * Help command
 *
 * @constructor Create empty Help command
 */
class HelpCommand : Command() {

    override fun execute(args: List<String>): Boolean {
        println("Help command!")
        return true
    }
}