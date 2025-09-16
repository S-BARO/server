package com.dh.baro.core.exception

import com.dh.baro.core.ErrorMessage

class InventoryInsufficientException(
    val productId: Long,
) : ConflictException(ErrorMessage.OUT_OF_STOCK.format(productId))
