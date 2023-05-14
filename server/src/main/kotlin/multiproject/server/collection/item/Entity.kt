package multiproject.server.collection.item

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
    open var creationDate: @Serializable(with=ZonedDateTimeSerializer::class)ZonedDateTime = ZonedDateTime.now()

    @Transient open var fieldsSchema: Map<String, CommandArgumentDto> = mapOf()
    @Transient open var pureData: Map<String, Any?> = mapOf()

    abstract val tableName: String
}