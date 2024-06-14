package com.sanryoo.shopping.feature.presentation.using.cart

import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product

sealed class CartUiEvent {
    object BackToPrevScreen: CartUiEvent()
    data class SetShowBottomSheet(val status: Boolean): CartUiEvent()
    data class NavigateToProduct(val product: Product): CartUiEvent()
    data class CheckOut(val orders: List<Order>): CartUiEvent()
}
