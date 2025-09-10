package com.dh.baro.core

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class LongToStringSerializer : JsonSerializer<Long>() {
    override fun serialize(value: Long?, gen: JsonGenerator, serializers: SerializerProvider) {
        if (value != null) {
            gen.writeString(value.toString())
        } else {
            gen.writeNull()
        }
    }
}
