package com.dh.baro.core.serialization

interface EventSerializer {
    fun serialize(event: Any): String
}
