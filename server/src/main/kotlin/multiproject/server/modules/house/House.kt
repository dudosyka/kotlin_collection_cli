package multiproject.server.modules.house

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.server.collection.item.Entity
import multiproject.server.collection.item.ZonedDateTimeSerializer
import java.time.ZonedDateTime

@Serializable
class House(
    override var id: Int,
    @Serializable(with = ZonedDateTimeSerializer::class) override var creationDate: ZonedDateTime,
    var name: String?,
    private var year: Long,
    private var numberOfFloors: Long,
    private var numberOfFlatsOnFloor: Long,
    private var numberOfLifts: Long,
    @Transient override val fieldsSchema: Map<String, CommandArgumentDto> = mapOf(),
    @Transient override val pureData: Map<String, Any?> = mapOf(),
) :Entity() {
    override val tableName: String
        get() = "house"
    override fun toString(): String {
        return "House {" +
                " name=$name," +
                " year=$year, " +
                " number of floors=$numberOfFloors, " +
                " number of flats on floor=$numberOfFlatsOnFloor, " +
                " number of lifts=$numberOfLifts " +
                "}"
    }
}