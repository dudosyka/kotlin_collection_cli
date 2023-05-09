package multiproject.server.exceptions

class BadJwtSubject: Exception() {
    override val message: String
        get() = "Token processing failed. Bad subject"
}