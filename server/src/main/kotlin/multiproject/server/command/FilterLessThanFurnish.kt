package multiproject.server.command

import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.FieldType
import multiproject.server.collection.item.Validator
import multiproject.server.flat.Furnish
import multiproject.udpsocket.dto.command.CommandArgumentDto
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Filter less than furnish
 *
 * @constructor Create empty Filter less than furnish
 */
class FilterLessThanFurnish : Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))

    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val furnish = args.firstOrNull()
        val validator = Validator(
            CommandArgumentDto(name = "number_of_rooms", type = multiproject.udpsocket.dto.command.FieldType.ENUM, required = true, choisable = Furnish.values().map { it.toString() })
        )

        if (!validator.validate(furnish))
            return CommandResult(validator.describe(), false)

        return CommandResult(collection.filterLessThanBy(furnish!!).toString())
}

}
