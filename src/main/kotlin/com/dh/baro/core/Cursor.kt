package com.dh.baro.core

import com.dh.baro.core.serialization.LongToStringSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize

class Cursor (
    @JsonSerialize(using = LongToStringSerializer::class)
    val id: Long,
)
