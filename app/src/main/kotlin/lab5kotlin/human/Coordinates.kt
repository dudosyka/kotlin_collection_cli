package lab5kotlin.human

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import lab5kotlin.collection.item.Entity
import java.time.ZonedDateTime

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