package lab5kotlin.collection.item

import lab5kotlin.collection.exceptions.ValidationFieldException

open class CollectionItem {
    var fields: MutableCollection<Field> = mutableListOf()
    var values: MutableMap<String, Any?> = mutableMapOf()

    fun init(values: MutableMap<String, Any?>) {
        for (field in fields) {
            val element = values[field.name]
            if (field.validate(element)) {
                this.values.put(field.name, element)
            } else {
                throw ValidationFieldException(field, element)
            }
        }
    }
}