package lab5kotlin.collection.item

class Field(name: String, type: FieldType, validationRule: ValidationRule) {
    var name: String
    var type: FieldType
    var validationRule: ValidationRule

    init {
        this.name = name
        this.type = type
        this.validationRule = validationRule
    }

    fun validate(value: Any?): Boolean {
        return this.validationRule.validate(this, value)
    }
}