package com.dh.baro.core

enum class ErrorMessage(val message: String) {
    UNHANDLED_EXCEPTION("UNHANDLED EXCEPTION"),
    NOT_FOUND("NOT FOUND"),
    UNAUTHORIZED("Unauthorized request"),
    BAD_REQUEST("Bad request"),
}
