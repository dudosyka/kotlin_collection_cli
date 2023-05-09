package multiproject.server.modules.flat

import kotlinx.serialization.Serializable

@Serializable
enum class Furnish {
    DESIGNER,
    NONE,
    FINE,
    LITTLE
}