package multiproject.lib.exceptions

class FileDumpException(val parent: Exception, val filePath: String, override val message: String): Exception()