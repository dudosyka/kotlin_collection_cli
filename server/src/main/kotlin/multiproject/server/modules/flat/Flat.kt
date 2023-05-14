package multiproject.server.modules.flat

import kotlinx.serialization.Serializable
import multiproject.server.collection.item.Entity
import multiproject.server.modules.house.House
import multiproject.server.modules.coordinates.Coordinates
import multiproject.server.modules.user.User

@Serializable
class Flat(
    override var id: Int,
    var name: String,
    private var area: Float,
    var numberOfRooms: Long,
    private var numberOfBathrooms: Long,
    var timeToMetroByTransport: Int,
    private var coordinates: Coordinates,
    var furnish: Furnish?,
    private var house: House?,
    private var author: User?,
): Entity() {
    override val tableName: String
        get() = "flat"

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
                "\tauthor=$author,\n" +
                "}"
    }
}