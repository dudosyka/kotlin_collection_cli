package lab5kotlin.human

import kotlinx.serialization.Serializable
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.ZonedDateTimeSerializer
import java.time.ZonedDateTime

/**
 * Human
 *
 * @property id
 * @property name
 * @property fatness
 * @property position
 * @property creationDate
 * @constructor Create empty Human
 */
@Serializable
class Human(
    override var id: Int,
    var name: String?,
    private var fatness: Fatness?,
    private var position: Coordinates,
    @Serializable(with = ZonedDateTimeSerializer::class) override var creationDate: ZonedDateTime
) : Entity() {
    override fun toString(): String {
        return "Human {\n" +
                "\tid=${id},\n" +
                "\tname=${name},\n" +
                "\tfatness=${fatness},\n" +
                "\tposition=${position}\n" +
                "\tcreatedAt=${creationDate}\n" +
                "}"
    }
}