package com.dh.baro.cart.presentation

import com.dh.baro.cart.application.CartFacade
import com.dh.baro.cart.presentation.dto.AddItemRequest
import com.dh.baro.cart.presentation.dto.CartResponse
import com.dh.baro.cart.presentation.dto.UpdateQuantityRequest
import com.dh.baro.cart.presentation.swagger.CartSwagger
import com.dh.baro.core.annotation.CheckAuth
import com.dh.baro.core.annotation.CurrentUser
import com.dh.baro.identity.domain.UserRole
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cart")
@CheckAuth(UserRole.BUYER)
class CartController(
    private val cartFacade: CartFacade,
) : CartSwagger {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun getCart(@CurrentUser userId: Long): CartResponse =
        CartResponse.from(cartFacade.getCartItems(userId))

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    override fun addItem(
        @CurrentUser userId: Long,
        @Valid @RequestBody request: AddItemRequest,
    ) = cartFacade.addItem(userId, request)

    @PatchMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun updateQuantity(
        @CurrentUser userId: Long,
        @PathVariable itemId: Long,
        @Valid @RequestBody request: UpdateQuantityRequest,
    ) = cartFacade.updateQuantity(userId, itemId, request.quantity)
    
    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun removeItem(
        @CurrentUser userId: Long,
        @PathVariable itemId: Long,
    ) = cartFacade.removeItem(userId, itemId)
}
