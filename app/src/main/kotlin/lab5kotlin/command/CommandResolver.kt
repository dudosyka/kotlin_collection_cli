package lab5kotlin.command

/**
 * Command resolver
 *
 * @constructor Create empty Command resolver
 */
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
        "load" to LoadCommand(),
        "execute_script" to ExecuteScriptCommand(),
        "exit" to ExitCommand(),
        "remove_at" to RemoveAtCommand(),
        "add_if_max" to AddIfMaxCommand(),
        "reorder" to ReorderCommand(),
        "count_by_number_of_rooms" to CountByNumberOfRoomsCommand(),
        "count_less_than_time_to_metro_by_transport" to CountLessThanTimeToMetroByTransportCommand(),
        "filter_less_than_furnish" to FilterLessThanFurnish(),
    )

    /**
     * Handle
     *
     * @param commandLine
     * @return
     */
    fun handle(commandLine: String): CommandResult? {
        val split = commandLine.split(" ")
        val name = split[0]
        val args = split.subList(1, split.size)
        val command: Command = commands[name] ?: return CommandResult("Command not found!",false)
        if (command.needObject) {
            val builder = ObjectBuilder(command.fields)
            return command.execute(args, builder.getEntityData())
        }
        return command.execute(args)
    }
}
