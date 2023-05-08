package multiproject.server.command

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.udp.server.router.Controller
import multiproject.lib.utils.ExecutableInput
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.entities.flat.Flat
import multiproject.server.entities.flat.RoomsComparator
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Add if max command
 *
 * @constructor Create empty Add if max command
 */
open class AddIfMaxCommand(controller: Controller) : AddCommand(controller) {
    private val entityBuilder: EntityBuilder<Flat> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))

    override val needObject: Boolean = true
    override val fields: Map<String, CommandArgumentDto> = entityBuilder.fields
    override val description: String = "Adds new element if number of rooms grater than max of current items"
    override fun execute(input: ExecutableInput): Response {
        val entity = this.entityBuilder.build(input.data)
        val result = collection.addIfMax(RoomsComparator(), entity)

        return if (!result) {
            Response(ResponseDto(ResponseCode.VALIDATION_ERROR,"Failed! number of rooms is lower than max in collection"))
        } else {
            Response(ResponseDto(ResponseCode.SUCCESS, "Item successfully created"))
        }
    }
}