package lab5kotlin.flat

import kotlinx.serialization.Serializable
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.ZonedDateTimeSerializer
import lab5kotlin.house.House
import lab5kotlin.coordinates.Coordinates
import java.time.ZonedDateTime

@Serializable
class Flat(
    override var id: Int,
    @Serializable(with = ZonedDateTimeSerializer::class) override var creationDate: ZonedDateTime,
    var name: String,
    private var area: Float,
    var numberOfRooms: Long,
    private var numberOfBathrooms: Long,
    var timeToMetroByTransport: Int,
    private var coordinates: Coordinates,
    var furnish: Furnish?,
    private var house: House?
): Entity() {
    override fun toString(): String {
        return "Flat {\n" +
                "\tid=$id,\n" +
                "\tname=$name,\n" +
                "\tarea=$area,\n" +
                "\tnumber of rooms=$numberOfRooms,\n" +
                "\tnumber of bathrooms=$numberOfBathrooms,\n" +
                "\ttime to metro by transport=$timeToMetroByTransport,\n" +
                "\tcoordinates=$coordinates,\n" +
                "\tfurnish=$furnish,\n" +
                "\thouse=$house,\n" +
                "\tcreationDate=$creationDate,\n" +
                "}"
    }
}