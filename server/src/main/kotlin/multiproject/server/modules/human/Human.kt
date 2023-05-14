package multiproject.server.modules.human

import kotlinx.serialization.Serializable
import multiproject.server.collection.item.Entity
import multiproject.server.modules.coordinates.Coordinates

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