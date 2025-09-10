package com.dh.baro.core

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.databind.type.TypeFactory

class StringListToLongListDeserializer : JsonDeserializer<List<Long>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): List<Long> {
        val typeFactory = TypeFactory.defaultInstance()
        val collectionType: CollectionType = typeFactory.constructCollectionType(List::class.java, String::class.java)
        val stringList: List<String> = p.codec.readValue(p, collectionType)
        return stringList.map { it.toLong() }
    }
}
