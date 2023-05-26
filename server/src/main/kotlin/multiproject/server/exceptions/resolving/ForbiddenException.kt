package multiproject.server.exceptions.resolving

import multiproject.lib.dto.response.ResponseCode
import multiproject.lib.exceptions.MiddlewareException
import multiproject.lib.request.Request

class ForbiddenException(request: Request): MiddlewareException(ResponseCode.FORBIDDEN, "Resource is forbidden", request)