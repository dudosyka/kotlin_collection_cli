package lab5kotlin.human

import kotlinx.serialization.Serializable

/**
 * Fatness
 *
 * @constructor Create empty Fatness
 */
@Serializable
enum class Fatness {
    /**
     * Fat
     *
     * @constructor Create empty Fat
     */
    FAT,

    /**
     * Skinny
     *
     * @constructor Create empty Skinny
     */
    SKINNY;

    /**
     * Values
     *
     * @return
     */
    fun values(): List<String> {
        val values = listOf<String>()
        for (item in Fatness.values()) {
            values.plus(item.toString())
        }
        return values
    }
}