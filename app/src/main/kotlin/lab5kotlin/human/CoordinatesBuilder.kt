package lab5kotlin.human

import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.collection.item.FieldDelegate
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator

/**
 * Coordinates builder
 *
 * @constructor Create empty Coordinates builder
 */
class CoordinatesBuilder : EntityBuilder<Coordinates>() {
    override val fields = mapOf(
        "x" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.INT,
        )),
        "y" to Validator(mapOf(
            "required" to true,
            "type" to FieldType.INT
        ))
    )

    override fun build(map: MutableMap<String, Any?>): Coordinates {
        val x: Int? by FieldDelegate(map = map, fields["x"]!!)
        val y: Int? by FieldDelegate(map = map, fields["y"]!!)
        return Coordinates(x!!, y!!)
    }
}
