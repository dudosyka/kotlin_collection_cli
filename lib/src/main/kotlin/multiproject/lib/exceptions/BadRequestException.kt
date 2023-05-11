package multiproject.lib.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.request.resolver.ResolveError

class BadRequestException(override val message: String): ResolveError(ResponseCode.BAD_REQUEST) {
}