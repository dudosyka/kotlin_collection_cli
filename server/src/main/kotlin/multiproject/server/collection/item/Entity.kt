package multiproject.server.collection.item

import kotlinx.serialization.Serializable
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

    open fun update(tableName: String, onUpdate: Map<String, Any>) {

    }
}