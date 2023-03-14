package lab5kotlin.human

import kotlinx.serialization.Transient
import lab5kotlin.collection.Collection
import lab5kotlin.collection.item.EntityBuilder
import lab5kotlin.collection.item.FieldDelegate
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
import java.time.ZonedDateTime

class HumanBuilder : EntityBuilder<Human>() {
    private val collection: Collection<Human> by KoinJavaComponent.inject(Collection::class.java, named("collection"))
    @Transient
    override val fields: MutableMap<String, Validator> = mutableMapOf(
        "name" to Validator(mapOf(
            "required" to false,
            "type" to FieldType.STRING
        )),
        "fatness" to Validator(mapOf(
            "required" to false,
            "type" to FieldType.ENUM,
            "childEnum" to "Fatness",
            "childEnumVariants" to Fatness.values().map { it.toString() }
        )),
        "position" to Validator(mapOf(
            "type" to FieldType.ENTITY,
            "childEntity" to CoordinatesBuilder(),
            "required" to true
        ))
    )
    override fun build(map: MutableMap<String, Any?>): Human {
        val id: Int = collection.getUniqueId()
        val name: String? by FieldDelegate(map = map, fields["name"]!!)
        val fatness: String? by FieldDelegate<String>(map = map, fields["fatness"]!!)
        var fatnessValue: Fatness? = null
        if (fatness != null)
           fatnessValue = Fatness.valueOf(fatness!!)
        val creationDate = ZonedDateTime.now()
        val position: MutableMap<String, Any?>? by FieldDelegate(map = map, fields["position"]!!)
        return Human(id, name, fatnessValue, CoordinatesBuilder().build(position!!), creationDate)
    }
}