package multiproject.server.exceptions

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.request.middleware.MiddlewareException

class ForbiddenException: MiddlewareException(ResponseCode.FORBIDDEN, "Resource is forbidden")