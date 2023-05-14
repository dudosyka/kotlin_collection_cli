package multiproject.server.modules.house

import kotlinx.serialization.Serializable
import multiproject.server.collection.item.Entity

@Serializable
class House(
    override var id: Int,
    var name: String?,
    private var year: Long,
    private var numberOfFloors: Long,
    private var numberOfFlatsOnFloor: Long,
    private var numberOfLifts: Long,
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