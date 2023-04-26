package multiproject.server.house

import multiproject.server.collection.Collection
import multiproject.server.collection.item.EntityBuilder
import multiproject.udpsocket.dto.command.FieldType
import multiproject.server.human.Human
import multiproject.udpsocket.dto.command.CommandArgumentDto
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import java.time.ZonedDateTime

class HouseBuilder: EntityBuilder<House>() {
    private val collection: Collection<Human> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    @Transient
    override val fields: MutableMap<String, CommandArgumentDto> = mutableMapOf(
        "name" to CommandArgumentDto(
            name = "name",
            required = false,
            type = FieldType.STRING
        ),
        "year" to CommandArgumentDto(
            name = "year",
            required = true,
            type = FieldType.LONG,
            min = 0
        ),
        "numberOfFloors" to CommandArgumentDto(
            name = "numberOfFloors",
            required = true,
            type = FieldType.LONG,
            min = 0
        ),
        "numberOfFlatsOnFloor" to CommandArgumentDto(
            name = "numberOfFlatsOnFloor",
            required = true,
            type = FieldType.LONG,
            min = 0
        ),
        "numberOfLifts" to CommandArgumentDto(
            name = "numberOfLifts",
            required = true,
            type = FieldType.LONG,
            min = 0
        ),
    )

    /**
     * Build
     *
     * @param map
     * @return
     */
    override fun build(map: MutableMap<String, Any?>): House {
        val id: Int = collection.getUniqueId()
        return House(1,ZonedDateTime.now(),"", 1L, 2L, 4L, 5L)
//        val name: String? by FieldDelegate(map = map, fields["name"]!!)
//        val year: Long? by FieldDelegate(map = map, fields["year"]!!)
//        val numberOfFloors: Long? by FieldDelegate(map = map, fields["numberOfFloors"]!!)
//        val numberOfFlatsOnFloor: Long? by FieldDelegate(map = map, fields["numberOfFlatsOnFloor"]!!)
//        val numberOfLifts: Long? by FieldDelegate(map = map, fields["numberOfLifts"]!!)
//        return House(id, ZonedDateTime.now(), name, year!!, numberOfFloors!!, numberOfFlatsOnFloor!!, numberOfLifts!!)
    }
}