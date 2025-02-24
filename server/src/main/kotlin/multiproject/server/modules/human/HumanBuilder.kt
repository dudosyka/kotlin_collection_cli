package multiproject.server.modules.human

import kotlinx.serialization.Transient
import multiproject.lib.dto.command.CommandArgumentDto
import multiproject.lib.dto.command.FieldType
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.collection.item.FieldDelegate
import multiproject.server.modules.coordinates.CoordinatesBuilder

/**
 * Human builder
 *
 * @constructor Create empty Human builder
 */
class HumanBuilder : EntityBuilder<Human>() {
    override val tableName: String
        get() = "human"

    @Transient
    override val fields: MutableMap<String, CommandArgumentDto> = mutableMapOf(
        "id" to CommandArgumentDto(
            name = "id",
            autoIncrement = true,
                show = false,
                type = FieldType.LONG
        ),
        "name" to CommandArgumentDto(
            name = "name",
            required = false,
            type = FieldType.STRING
        ),
        "fatness" to CommandArgumentDto(
            name = "fatness",
            required = false,
            type = FieldType.ENUM,
            choisable = Fatness.values().map { it.toString() }
        ),
        "position" to CommandArgumentDto(
            name = "position",
            required = true,
            nested = mapOf(
                "x" to CommandArgumentDto(name = "x", type = FieldType.INT),
                "y" to CommandArgumentDto(name = "x", type = FieldType.INT)
            )
        )
    )
    override fun build(map: MutableMap<String, Any?>): Human {
        val id: Long? by FieldDelegate(map = map, fields["id"]!!)
        val name: String? by FieldDelegate(map = map, fields["name"]!!)
        val fatness: String? by FieldDelegate<String>(map = map, fields["fatness"]!!)
        val fatnessValue: Fatness = Fatness.valueOf(fatness!!)
        val position: MutableMap<String, Any?>? by FieldDelegate(map = map, fields["position"]!!)
        return Human(id!!.toInt(), name, fatnessValue, CoordinatesBuilder().build(position!!)).apply {
            pureData = map
            fieldsSchema = fields
        }
    }
}