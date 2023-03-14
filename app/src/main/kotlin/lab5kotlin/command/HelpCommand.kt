package lab5kotlin.command

class HelpCommand : Command() {

    override fun execute(args: List<String>): Boolean {
        println("Help command!")
        return true
    }
}