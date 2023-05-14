package multiproject.server.modules.coordinates

import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.collection.item.FieldDelegate

/**
 * Coordinates builder
 *
 * @constructor Create empty Coordinates builder
 */
class CoordinatesBuilder : EntityBuilder<Coordinates>() {
    override val tableName: String
        get() = "coordinates"

    override val fields = mapOf(
        "id" to CommandArgumentDto(name = "id", type = FieldType.INT, show = false, autoIncrement = true),
        "x" to CommandArgumentDto(name = "x", type = FieldType.INT, min = 0),
        "y" to CommandArgumentDto(name = "x", type = FieldType.INT, min = 0)
    )

    override fun build(map: MutableMap<String, Any?>): Coordinates {
        val x: Long? by FieldDelegate(map = map, fields["x"]!!)
        val y: Long? by FieldDelegate(map = map, fields["y"]!!)
        return Coordinates(x?.toInt()!!, y?.toInt()!!).apply {
            fieldsSchema = fields
            pureData = map
        }
    }
}
