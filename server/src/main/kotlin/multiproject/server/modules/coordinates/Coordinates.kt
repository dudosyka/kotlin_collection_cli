package multiproject.server.modules.coordinates

import kotlinx.serialization.Serializable
import multiproject.server.collection.item.Entity

/**
 * Coordinates
 *
 * @property x
 * @property y
 * @property id
 * @property creationDate
 * @constructor Create empty Coordinates
 */
@Serializable
class Coordinates(private var x: Int, private var y: Int, override var id: Int = 0) : Entity() {

    override val tableName: String
        get() = "coordinates"
    override fun toString(): String {
        return "Coordinates {" +
                " x=${x}," +
                " y=${y} " +
                "}"
    }

}