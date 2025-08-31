package com.dh.baro.core.event

interface EventSerializer {
    fun serialize(event: Any): String
}
