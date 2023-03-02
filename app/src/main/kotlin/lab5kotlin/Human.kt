package lab5kotlin

import lab5kotlin.collection.item.CollectionItem
import lab5kotlin.collection.item.Field
import lab5kotlin.collection.item.FieldType
import lab5kotlin.collection.item.ValidationRule

class Human : CollectionItem() {
    init {
        val idValidationRule = ValidationRule(null, null, true)
        val idField = Field("id", FieldType.NUMBER, idValidationRule)

        val nameValidationRule = ValidationRule(null, null, true)
        val nameField = Field("name", FieldType.STRING, nameValidationRule)

        val ageValidationRule = ValidationRule(null, null, true)
        val ageField = Field("age", FieldType.LONG, ageValidationRule)


        this.fields.add(idField)
        this.fields.add(nameField)
        this.fields.add(ageField)

        this.values.put("id", null)
        this.values.put("name", null)
        this.values.put("age", null)
    }
}