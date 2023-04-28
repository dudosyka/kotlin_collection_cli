package multiproject.server.command

import multiproject.lib.dto.command.CommandResult

/**
 * Help command
 *
 * @constructor Create empty Help command
 */
class HelpCommand : Command() {
    override val description: String = "Show help text"
    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val help = CommandResolver.commands.filter { !it.value.hideFromClient }.map { "${it.key} ${it.value.getHelpString()}" }.joinToString("\n")
//        "" +
//                "help - Show this text\n" +
//                "info - Show information about collection\n" +
//                "show - Show items in collection\n" +
//                "add - Adds new item to collection\n" +
//                "update {id} - Update element with specified id\n" +
//                "remove_by_id {id} - Remove element with specified id\n" +
//                "clear - Clear the collection\n" +
//                "save - Dump collection to the file\n" +
//                "load - Restore collection from the file\n" +
//                "execute_script {path} - Run script\n" +
//                "remove_at {index} - Remove element on specified index\n" +
//                "add_if_max - Adds new element if number of rooms grater than max of current items\n" +
//                "reorder - Sort items and show collection\n" +
//                "count_by_number_of_rooms {numberOfRooms} - Show number of items which have that number of rooms\n" +
//                "count_less_than_time_to_metro_by_transport {timeToMetro} - Show number of items which time to metro less than specified\n" +
//                "filter_less_than_furnish {furnish} - Show number of items that which furniture less than specified"
        return CommandResult(help)
    }
}