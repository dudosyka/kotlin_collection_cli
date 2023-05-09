package multiproject.lib.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.utils.ExecuteException

class EmptyRequestProvided: ExecuteException(ResponseCode.BAD_REQUEST) {
    override val message: String
        get() = "Error! Request without body!"
}