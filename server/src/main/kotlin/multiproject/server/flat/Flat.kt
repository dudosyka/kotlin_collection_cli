package multiproject.server.flat

import kotlinx.serialization.Serializable
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.ZonedDateTimeSerializer
import multiproject.server.house.House
import multiproject.server.coordinates.Coordinates
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