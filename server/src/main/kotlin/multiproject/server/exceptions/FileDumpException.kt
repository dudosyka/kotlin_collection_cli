package multiproject.server.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.utils.ExecuteException

class FileDumpException(val parent: Exception, val filePath: String, override val message: String): ExecuteException(ResponseCode.INTERNAL_SERVER_ERROR)