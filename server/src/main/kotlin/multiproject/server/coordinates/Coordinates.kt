package multiproject.server.coordinates

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import multiproject.server.collection.item.Entity
import java.time.ZonedDateTime

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
class Coordinates(private var x: Int, private var y: Int, override var id: Int = 0,
                  @Transient override var creationDate: ZonedDateTime = ZonedDateTime.now()
) : Entity() {
    override fun toString(): String {
        return "Coordinates {" +
                " x=${x}," +
                " y=${y} " +
                "}"
    }

}