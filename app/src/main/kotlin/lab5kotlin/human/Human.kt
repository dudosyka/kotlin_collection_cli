package lab5kotlin.human

import kotlinx.serialization.Serializable
import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.FieldDelegate
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.Validator

//, var age: Int, var fatness: Fatness, var position: CoordinatesDto

class HumanBuilder(map: MutableMap<String, Any?>) {
    var id: Int? by FieldDelegate(map = map, Validator(mapOf(
        "required" to true,
        "type" to FieldType.INT,
        "max" to 10
    )))
    private val name: String? by FieldDelegate(map = map, Validator(mapOf(
        "required" to true,
        "type" to FieldType.STRING
    )))
    private val position: Coordinates? by FieldDelegate(map = map, Validator(mapOf(
        "type" to FieldType.ENTITY,
        "required" to true
    )))

    fun build(): Human {
        return Human(id!!, name!!, position!!)
    }
}

@Serializable
class Human(private var id: Int, private var name: String, private var position: Coordinates) : Entity() {
    override fun toString(): String {
        return "Human {\n" +
                "\tid=${id},\n" +
                "\tname=${name},\n" +
                "\tposition=${position}\n" +
                "}"
    }
}