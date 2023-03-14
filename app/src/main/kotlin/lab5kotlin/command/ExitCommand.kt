package lab5kotlin.command

class ExitCommand : Command() {
    override fun execute(args: List<String>): Boolean {
        return false
    }
}