package lab5kotlin.human

import kotlinx.serialization.Serializable

@Serializable
enum class Fatness {
    FAT, SKINNY;
    fun values(): List<String> {
        val values = listOf<String>()
        for (item in Fatness.values()) {
            values.plus(item.toString())
        }
        return values
    }
}