package multiproject.server.command

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.command.FieldType
import multiproject.lib.dto.command.Validator
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.CommandSyncType
import multiproject.lib.udp.server.router.Controller
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.modules.flat.Furnish
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

/**
 * Filter less than furnish
 *
 * @constructor Create empty Filter less than furnish
 */
class FilterLessThanFurnish(controller: Controller) : Command(controller) {
    private val collection: Collection<Entity> by KoinJavaComponent.inject(Collection::class.java, named("collection"))

    override val fields: Map<String, CommandArgumentDto> = mapOf(
        "filter" to CommandArgumentDto(
            name = "filter",
            inline = true,
            required = true,
            index = 0,
            type = FieldType.ENUM,
            choisable = Furnish.values().map { it.toString() }
        )
    )
    override val description: String = "Show items with furniture less than specified"
    override val commandSyncType: CommandSyncType
        get() = CommandSyncType(true)

    override suspend fun execute(input: ExecutableInput): Response {
        val furnish = input.args.firstOrNull()
        val validator = Validator(
            CommandArgumentDto(name = "number_of_rooms", type = FieldType.ENUM, required = true, choisable = Furnish.values().map { it.toString() })
        )

        if (!validator.validate(furnish))
            return Response(ResponseCode.VALIDATION_ERROR, validator.describe())

//        collection.filterLessThanBy(furnish!!)
        return Response(ResponseCode.SUCCESS, collection.filterLessThanBy(furnish!!))
}

}
