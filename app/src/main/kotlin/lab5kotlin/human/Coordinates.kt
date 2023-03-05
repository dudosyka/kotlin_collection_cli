package lab5kotlin.human

import kotlinx.serialization.Serializable
import lab5kotlin.collection.item.Entity

class CoordinatesBuilder(values: MutableMap<String, Any?>) {
    private val x: Int by values
    private val y: Int by values
    fun build(): Coordinates {
        return Coordinates(x, y)
    }
}

@Serializable
class Coordinates(private var x: Int, private var y: Int) : Entity() {

    init {
//        val validator = Validator(null, null, true)
//        val xField = Field("x", FieldType.NUMBER, validator)
//        val yField = Field("y", FieldType.NUMBER, validator)
//
//        this.fields.add(xField)
//        this.fields.add(yField)
//
//        this.init(values)
    }

    override fun toString(): String {
        return "Coordinates {" +
                " x=${x}," +
                " y=${y} " +
                "}"
    }

}