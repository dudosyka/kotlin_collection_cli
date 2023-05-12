package multiproject.server.collection.item

import kotlinx.serialization.Serializable
import multiproject.lib.dto.command.CommandArgumentDto
import java.time.ZonedDateTime

/**
 * Entity
 *
 * @constructor Create empty Entity
 */
@Serializable
abstract class Entity {
    abstract var id: Int
    abstract var creationDate: ZonedDateTime

    abstract val fieldsSchema: Map<String, CommandArgumentDto>
    abstract val pureData: Map<String, Any?>

    abstract val tableName: String
}