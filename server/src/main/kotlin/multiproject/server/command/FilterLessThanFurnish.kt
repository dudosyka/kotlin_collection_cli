package multiproject.server.command

import multiproject.lib.dto.command.Validator
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.entities.flat.Furnish
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.CommandResult
import multiproject.lib.dto.command.FieldType
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Filter less than furnish
 *
 * @constructor Create empty Filter less than furnish
 */
class FilterLessThanFurnish : Command() {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))

    override val fields: Map<String, CommandArgumentDto> = mapOf(
        "filter" to CommandArgumentDto(
            name = "filter",
            inline = true,
            required = true,
            index = 0,
            type = FieldType.INT,
        )
    )
    override val description: String = "Show number of items that which furniture less than specified"

    override fun execute(args: List<Any?>, data: MutableMap<String, Any?>): CommandResult {
        val furnish = args.firstOrNull()
        val validator = Validator(
            CommandArgumentDto(name = "number_of_rooms", type = FieldType.ENUM, required = true, choisable = Furnish.values().map { it.toString() })
        )

        if (!validator.validate(furnish))
            return CommandResult(validator.describe(), false)

        return CommandResult(collection.filterLessThanBy(furnish!!).toString())
}

}
