package lab5kotlin.collection.item

abstract class EntityBuilder<T: Entity> {

    abstract val fields: Map<String, Validator>
    abstract fun build(map: MutableMap<String, Any?>): T
}