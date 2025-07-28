package com.dh.baro.core.converter

import com.dh.baro.order.domain.OrderStatus
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class OrderStatusConverter : AttributeConverter<OrderStatus, String> {
    override fun convertToDatabaseColumn(attribute: OrderStatus): String =
        attribute.name

    override fun convertToEntityAttribute(dbData: String): OrderStatus =
        OrderStatus.valueOf(dbData)
}
