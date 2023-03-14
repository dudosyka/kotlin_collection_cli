package lab5kotlin.collection.item

/**
 * Entity builder
 *
 * @param T
 * @constructor Create empty Entity builder
 */
abstract class EntityBuilder<T: Entity> {

    abstract val fields: Map<String, Validator>

    /**
     * Build
     *
     * @param map
     * @return
     */
    abstract fun build(map: MutableMap<String, Any?>): T
}