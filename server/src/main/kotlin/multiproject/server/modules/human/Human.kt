package multiproject.server.modules.human

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.ZonedDateTimeSerializer
import multiproject.server.modules.coordinates.Coordinates
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
    @Serializable(with = ZonedDateTimeSerializer::class) override var creationDate: ZonedDateTime,
    @Transient override val fieldsSchema: Map<String, CommandArgumentDto> = mapOf(),
    @Transient override val pureData: Map<String, Any?> = mapOf(),
) : Entity() {
    override val tableName: String
        get() = "human"
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