package multiproject.server.collection.item

import multiproject.udpsocket.dto.command.CommandArgumentDto

/**
 * Entity builder
 *
 * @param T
 * @constructor Create empty Entity builder
 */
abstract class EntityBuilder<T: Entity> {

    abstract val fields: Map<String, CommandArgumentDto>

    /**
     * Build
     *
     * @param map
     * @return
     */
    abstract fun build(map: MutableMap<String, Any?>): T
}