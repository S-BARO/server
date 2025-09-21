package com.dh.baro.core.exception

import com.dh.baro.core.ErrorMessage

class InventoryInsufficientException(
    val productId: Long,
    val requestedQuantity: Int,
) : ConflictException(ErrorMessage.OUT_OF_STOCK.format(productId))
