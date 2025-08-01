package com.dh.baro.cart.presentation

import com.dh.baro.cart.application.CartFacade
import com.dh.baro.cart.presentation.dto.AddItemRequest
import com.dh.baro.cart.presentation.dto.CartResponse
import com.dh.baro.cart.presentation.dto.UpdateQuantityRequest
import com.dh.baro.core.auth.Authenticated
import com.dh.baro.core.auth.CurrentUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cart")
@Authenticated
class CartController(
    private val cartFacade: CartFacade
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getCart(@CurrentUser userId: Long): CartResponse =
        CartResponse.from(cartFacade.getCartItems(userId))

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    fun addItem(
        @CurrentUser userId: Long,
        @Valid @RequestBody request: AddItemRequest,
    ) = cartFacade.addItem(userId, request)

    @PatchMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateQuantity(
        @CurrentUser userId: Long,
        @PathVariable itemId: Long,
        @Valid @RequestBody request: UpdateQuantityRequest,
    ) = cartFacade.updateQuantity(userId, itemId, request.quantity)
    
    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeItem(
        @CurrentUser userId: Long,
        @PathVariable itemId: Long,
    ) = cartFacade.removeItem(userId, itemId)
}
