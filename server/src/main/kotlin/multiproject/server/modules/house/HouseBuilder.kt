package multiproject.server.modules.house

import multiproject.server.collection.Collection
import multiproject.server.collection.item.EntityBuilder
import multiproject.lib.dto.command.FieldType
import multiproject.server.modules.human.Human
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.server.collection.item.FieldDelegate
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent

class HouseBuilder: EntityBuilder<House>() {
    override val tableName: String
        get() = "house"
    private val collection: Collection<Human> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    @Transient
    override val fields: MutableMap<String, CommandArgumentDto> = mutableMapOf(
        "id" to CommandArgumentDto(name = "id", type = FieldType.INT, show = false, autoIncrement = true),
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
        val name: String? by FieldDelegate(map = map, fields["name"]!!)
        val year: Long? by FieldDelegate(map = map, fields["year"]!!)
        val numberOfFloors: Long? by FieldDelegate(map = map, fields["numberOfFloors"]!!)
        val numberOfFlatsOnFloor: Long? by FieldDelegate(map = map, fields["numberOfFlatsOnFloor"]!!)
        val numberOfLifts: Long? by FieldDelegate(map = map, fields["numberOfLifts"]!!)
        return House(id, name, year!!, numberOfFloors!!, numberOfFlatsOnFloor!!, numberOfLifts!!).apply {
            pureData = map
            fieldsSchema = fields
        }
    }
}