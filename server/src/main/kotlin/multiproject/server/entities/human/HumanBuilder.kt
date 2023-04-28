package multiproject.server.entities.human

import kotlinx.serialization.Transient
import multiproject.server.collection.item.EntityBuilder
import multiproject.server.collection.item.FieldDelegate
import multiproject.server.entities.coordinates.CoordinatesBuilder
import multiproject.server.collection.Collection
import multiproject.lib.dto.command.CommandArgumentDto
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import java.time.ZonedDateTime

/**
 * Human builder
 *
 * @constructor Create empty Human builder
 */
class HumanBuilder : EntityBuilder<Human>() {
    private val collection: Collection<Human> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    @Transient
    override val fields: MutableMap<String, CommandArgumentDto> = mutableMapOf(
        "name" to CommandArgumentDto(
            name = "name",
            required = false,
            type = multiproject.lib.dto.command.FieldType.STRING
        ),
        "fatness" to CommandArgumentDto(
            name = "fatness",
            required = false,
            type = multiproject.lib.dto.command.FieldType.ENUM,
            choisable = Fatness.values().map { it.toString() }
        ),
        "position" to CommandArgumentDto(
            name = "position",
            required = true,
            nested = mapOf(
                "x" to CommandArgumentDto(name = "x", type = multiproject.lib.dto.command.FieldType.INT),
                "y" to CommandArgumentDto(name = "x", type = multiproject.lib.dto.command.FieldType.INT)
            )
        )
    )
    override fun build(map: MutableMap<String, Any?>): Human {
        val id: Int = this.collection.getUniqueId()
        val name: String? by FieldDelegate(map = map, fields["name"]!!)
        val fatness: String? by FieldDelegate<String>(map = map, fields["fatness"]!!)
        val fatnessValue: Fatness = Fatness.valueOf(fatness!!)
        val creationDate = ZonedDateTime.now()
        val position: MutableMap<String, Any?>? by FieldDelegate(map = map, fields["position"]!!)
        return Human(id, name, fatnessValue, CoordinatesBuilder().build(position!!), creationDate)
    }
}