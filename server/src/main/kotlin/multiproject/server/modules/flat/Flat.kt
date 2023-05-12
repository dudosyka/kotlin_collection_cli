package multiproject.server.modules.flat

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.ZonedDateTimeSerializer
import multiproject.server.modules.house.House
import multiproject.server.modules.coordinates.Coordinates
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
    private var house: House?,
    var author: Long?,
    @Transient override val fieldsSchema: Map<String, CommandArgumentDto> = mapOf(),
    @Transient override val pureData: Map<String, Any?> = mapOf()
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
                "}"
    }
}