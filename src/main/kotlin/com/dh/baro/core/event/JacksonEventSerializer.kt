package com.dh.baro.core.event

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class JacksonEventSerializer(
    private val objectMapper: ObjectMapper
) : EventSerializer {

    override fun serialize(event: Any): String {
        return try {
            objectMapper.writeValueAsString(event)
        } catch (e: JsonProcessingException) {
            throw IllegalStateException("[Jackson] 이벤트 직렬화에 실패했습니다: ${e.message}")
        }
    }
}
