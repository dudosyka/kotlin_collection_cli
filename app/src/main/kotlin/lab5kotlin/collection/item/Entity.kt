package lab5kotlin.collection.item

import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
abstract class Entity {
    abstract var id: Int
    abstract var creationDate: ZonedDateTime
}