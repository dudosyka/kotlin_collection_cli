package multiproject.server.entities.flat

import kotlinx.serialization.Serializable

@Serializable
enum class Furnish {
    DESIGNER,
    NONE,
    FINE,
    LITTLE
}