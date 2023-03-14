package lab5kotlin.command

class CommandResolver {
    private val commands: Map<String, Command> = mapOf(
        "help" to HelpCommand(),
        "info" to InfoCommand(),
        "show" to ShowCommand(),
        "add" to AddCommand(),
        "update" to UpdateCommand(),
        "remove_by_id" to RemoveByIdCommand(),
        "clear" to ClearCommand(),
        "save" to SaveCommand(),
        "execute_script" to ExecuteScriptCommand(),
        "exit" to ExitCommand(),
        "remove_at" to RemoveAtCommand(),
        "add_if_max" to AddIfMaxCommand(),
        "reorder" to ReorderCommand(),
        "count_by_number_of_rooms" to CountByNumberOfRoomsCommand(),
        "count_less_than_time_to_metro_by_transport" to CountLessThanTimeToMetroByTransportCommand(),
        "filter_less_than_furnish" to FilterLessThanFurnish(),
    )
    fun handle(commandLine: String): Boolean? {
        val split = commandLine.split(" ")
        val name = split[0]
        val args = split.subList(1, split.size)
        val command: Command = commands[name] ?: return null
        return command.execute(args)
    }
}