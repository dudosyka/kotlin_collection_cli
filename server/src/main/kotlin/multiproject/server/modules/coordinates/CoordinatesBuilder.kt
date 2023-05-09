package multiproject.server.modules.coordinates

import multiproject.server.collection.item.EntityBuilder
import multiproject.server.collection.item.FieldDelegate
import multiproject.lib.dto.command.FieldType
import multiproject.lib.dto.command.CommandArgumentDto

/**
 * Coordinates builder
 *
 * @constructor Create empty Coordinates builder
 */
class CoordinatesBuilder : EntityBuilder<Coordinates>() {
    override val fields = mapOf(
        "x" to CommandArgumentDto(name = "x", type = FieldType.INT, min = 0),
        "y" to CommandArgumentDto(name = "x", type = FieldType.INT, min = 0)
    )

    override fun build(map: MutableMap<String, Any?>): Coordinates {
        val x: Long? by FieldDelegate(map = map, fields["x"]!!)
        val y: Long? by FieldDelegate(map = map, fields["y"]!!)
        return Coordinates(x?.toInt()!!, y?.toInt()!!)
    }
}
