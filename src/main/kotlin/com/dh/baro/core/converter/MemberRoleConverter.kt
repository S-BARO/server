package com.dh.baro.core.converter

import com.dh.baro.identity.domain.MemberRole
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class MemberRoleConverter : AttributeConverter<MemberRole, String> {
    override fun convertToDatabaseColumn(attribute: MemberRole): String {
        return attribute.name
    }

    override fun convertToEntityAttribute(dbData: String): MemberRole {
        return MemberRole.valueOf(dbData)
    }
}
