package multiproject.server.command

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.dto.response.ResponseDto
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.lib.utils.ExecutableInput
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

/**
 * Add command
 *
 * @constructor Create empty Add command
 */
open class AddCommand(controller: Controller) : Command(controller) {
    private val entityBuilder: EntityBuilder<Entity> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))

    override val needObject: Boolean = true
    override val fields: Map<String, CommandArgumentDto> = entityBuilder.fields
    override val description = "Adds new item to collection."

    /**
     * Execute
     *
     * @param args
     * @return
     */
    override fun execute(input: ExecutableInput): Response {
        val entity = this.entityBuilder.build(input.data)
        collection.addItem(entity)
        return Response(ResponseDto(ResponseCode.SUCCESS, "Item successfully created."))
    }
}