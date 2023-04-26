package multiproject.server.collection.item

/**
 * Field type
 *
 * @constructor Create empty Field type
 */
enum class FieldType {
    /**
     * String
     *
     * @constructor Create empty String
     */
    STRING,

    /**
     * Number
     *
     * @constructor Create empty Number
     */
    NUMBER,

    /**
     * Long
     *
     * @constructor Create empty Long
     */
    LONG,

    /**
     * Int
     *
     * @constructor Create empty Int
     */
    INT,

    FLOAT,

    /**
     * Entity
     *
     * @constructor Create empty Entity
     */
    ENTITY,

    /**
     * Enum
     *
     * @constructor Create empty Enum
     */
    ENUM,

    /**
     * Boolean
     *
     * @constructor Create empty Boolean
     */
    BOOLEAN
}