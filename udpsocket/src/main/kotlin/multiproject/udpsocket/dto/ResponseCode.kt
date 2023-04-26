package multiproject.udpsocket.dto

enum class ResponseCode {
    SUCCESS,
    BAD_REQUEST,
    NOT_FOUND,
    VALIDATION_ERROR,
    INTERNAL_SERVER_ERROR,
    CONNECTION_REFUSED
}