package com.dh.baro.core.outbox

enum class MessageStatus {
    INIT,
    SEND_SUCCESS,
    SEND_FAIL,
    DEAD,
}
