package multiproject.lib.exceptions.gateway

import multiproject.lib.dto.response.ResponseCode

class ResolveError(val code: ResponseCode): Exception() {

}