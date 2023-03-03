package lab5kotlin

import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.Field
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.ValidationRule

class Coordinates(values: MutableMap<String, Any?>) : Entity() {
    init {
        val validationRule = ValidationRule(null, null, true)
        val xField = Field("x", FieldType.NUMBER, validationRule)
        val yField = Field("y", FieldType.NUMBER, validationRule)

        this.fields.add(xField)
        this.fields.add(yField)

        this.init(values)
    }
}