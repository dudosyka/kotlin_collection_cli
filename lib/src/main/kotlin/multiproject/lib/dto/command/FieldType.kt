package multiproject.lib.dto.command

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