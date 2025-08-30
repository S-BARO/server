package com.dh.baro.product.infra.event

import com.dh.baro.product.domain.service.InventoryService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class InventoryEventHandler(
    private val inventoryService: InventoryService,
) {

    @Transactional
    fun handleEvent(event: InventoryDeductionRequestedEvent) {
        event.items.forEach { item ->
            inventoryService.deductInventory(item.productId, item.quantity)
        }
    }
}
