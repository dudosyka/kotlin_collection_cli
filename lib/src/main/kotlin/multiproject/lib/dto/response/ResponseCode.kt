package multiproject.lib.dto.response

enum class ResponseCode {
    SUCCESS,
    BAD_REQUEST,
    ITEM_NOT_FOUND,
    NOT_FOUND,
    VALIDATION_ERROR,
    INTERNAL_SERVER_ERROR,
    CONNECTION_REFUSED
}