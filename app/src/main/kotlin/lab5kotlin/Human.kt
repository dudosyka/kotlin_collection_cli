package lab5kotlin

import lab5kotlin.collection.item.Entity
import lab5kotlin.collection.item.Field
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.ValidationRule

class Human(values: MutableMap<String, Any?>) : Entity() {
    init {
        val validationRule = ValidationRule(null, null, true)

        val idField = Field("id", FieldType.NUMBER, validationRule)
        val nameField = Field("name", FieldType.STRING, validationRule)
        val ageField = Field("age", FieldType.LONG, validationRule)

        val positionValidationRule = ValidationRule(null, null, true, Coordinates::class.simpleName)
        val positionField = Field("position", FieldType.ENTITY, positionValidationRule)

        val fatnessValidationRule = ValidationRule(null, null, true, null, Fatness::class.simpleName)
        val fatnessField = Field("fatness", FieldType.ENUM, fatnessValidationRule)


        this.fields.add(idField)
        this.fields.add(nameField)
        this.fields.add(ageField)
        this.fields.add(positionField)
        this.fields.add(fatnessField)

        this.init(values)
    }
}