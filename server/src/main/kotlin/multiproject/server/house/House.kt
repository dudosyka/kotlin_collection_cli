package multiproject.server.house

import kotlinx.serialization.Serializable
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
) :Entity() {
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