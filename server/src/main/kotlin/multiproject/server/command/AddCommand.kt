package multiproject.server.command

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.CommitDto
import multiproject.lib.dto.command.ExecutableInput
import multiproject.lib.dto.response.Response
import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.udp.server.router.Command
import multiproject.lib.udp.server.router.Controller
import multiproject.server.collection.Collection
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.database.DatabaseManager
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import java.time.ZonedDateTime

/**
 * Add command
 *
 * @constructor Create empty Add command
 */
open class AddCommand(controller: Controller) : Command(controller) {
    private val entityBuilder: EntityBuilder<Entity> by inject(EntityBuilder::class.java, named("builder"))
    private val collection: Collection<Entity> by inject(Collection::class.java, named("collection"))
    private val dbManager: DatabaseManager by inject(DatabaseManager::class.java, named("dbManager"))
//    private val logger: Logger by inject(Logger::class.java, named())

    override val needObject: Boolean = true
    override val fields: Map<String, CommandArgumentDto> = entityBuilder.fields
    override val description = "Adds new item to collection."

    /**
     * Execute
     *
     * @param input
     * @return
     */
    override suspend fun execute(input: ExecutableInput): Response {
        val id = dbManager.getNewId(this.entityBuilder.tableName).toLong()
        input.data["id"] = id
        val entity = this.entityBuilder.build(input.data)
        collection.addItem(entity)
        return Response(ResponseCode.SUCCESS, "Item successfully created.", commits = listOf(
            CommitDto(
                id = id,
                timestamp = ZonedDateTime.now().toEpochSecond(),
                data = input.data
            )
        ))
    }
}