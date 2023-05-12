package multiproject.lib.exceptions

import multiproject.lib.dto.request.PathDto
import multiproject.lib.dto.response.ResponseCode

class RouteNotFound(private val pathDto: PathDto): ExecuteException(ResponseCode.NOT_FOUND) {
    override val message: String
        get() = "Route ${pathDto.controller}/${pathDto.route} not found!"
}