package multiproject.server.modules.flat

import kotlinx.serialization.Transient
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.collection.item.FieldDelegate
import multiproject.server.modules.coordinates.CoordinatesBuilder
import multiproject.server.modules.house.HouseBuilder
import multiproject.server.modules.user.UserBuilder

class FlatBuilder: EntityBuilder<Flat>() {

    override val tableName: String
        get() = "flat"

    @Transient
    override val fields: MutableMap<String, CommandArgumentDto> = mutableMapOf(
        "id" to CommandArgumentDto(name = "id", type = FieldType.INT, show = false, autoIncrement = true),
        "name" to CommandArgumentDto(
            name = "name",
            required = true,
            type = FieldType.STRING
        ),
        "area" to CommandArgumentDto(
            name = "area",
            required = true,
            type = FieldType.FLOAT,
            min = 0
        ),
        "numberOfRooms" to CommandArgumentDto(
            name = "numberOfRooms",
            required = true,
            type = FieldType.LONG,
            min = 0
        ),
        "numberOfBathrooms" to CommandArgumentDto(
            name = "numberOfBathrooms",
            required = true,
            type = FieldType.LONG,
            min = 0
        ),
        "timeToMetroByTransport" to CommandArgumentDto(
            name = "timeToMetroByTransport",
            required = true,
            type = FieldType.INT,
            min = 0
        ),
        "coordinates" to CommandArgumentDto(
            name = "coordinates",
            required = true,
            nested = CoordinatesBuilder().fields,
            nestedTable = CoordinatesBuilder().tableName,
            nestedJoinOn = Pair("coordinates", "id")
        ),
        "house" to CommandArgumentDto(
            name = "house",
            required = true,
            nested = HouseBuilder().fields,
            nestedTable = HouseBuilder().tableName,
            nestedJoinOn = Pair("house", "id")
        ),
        "author" to CommandArgumentDto(
            name = "author",
            type = FieldType.INT,
            show = false,
            required = false,
            autoIncrement = true,
            nested = UserBuilder().fields,
            nestedTable = UserBuilder().tableName,
            nestedJoinOn = Pair("author", "id")
        ),
        "furnish" to CommandArgumentDto(
            name = "furnish",
            required = false,
            type = FieldType.ENUM,
            choisable = Furnish.values().map { it.toString() }
        ),
    )

    /**
     * Build
     *
     * @param map
     * @return
     */
    override fun build(map: MutableMap<String, Any?>): Flat {
        val id: Long? by FieldDelegate(map = map, fields["id"]!!)
        val name: String? by FieldDelegate(map = map, fields["name"]!!)
        val area: Float? by FieldDelegate(map = map, fields["area"]!!)
        val numberOfRooms: Long? by FieldDelegate(map = map, fields["numberOfRooms"]!!)
        val numberOfBathrooms: Long? by FieldDelegate(map = map, fields["numberOfBathrooms"]!!)
        val timeToMetroByTransport: Long? by FieldDelegate(map = map, fields["timeToMetroByTransport"]!!)
        val furnish: String? by FieldDelegate(map = map, fields["furnish"]!!)
        val furnishValue = Furnish.valueOf(furnish!!)

        val coordinates: MutableMap<String, Any?>? by FieldDelegate(map = map, fields["coordinates"]!!)
        val coordinatesEntity = CoordinatesBuilder().build(coordinates!!)

        val house: MutableMap<String, Any?>? by FieldDelegate(map = map, fields["house"]!!)
        house?.set("id", id)
        val houseEntity = HouseBuilder().build(house!!)

        val author: MutableMap<String, Any?>? by FieldDelegate(map = map, fields["author"]!!)
        val userEntity = UserBuilder().build(author!!)

        return Flat(id!!.toInt(),name!!,area!!,numberOfRooms!!,numberOfBathrooms!!,timeToMetroByTransport?.toInt()!!, coordinatesEntity, furnishValue, houseEntity, userEntity).apply {
            pureData = map
            fieldsSchema = fields
        }
    }
}