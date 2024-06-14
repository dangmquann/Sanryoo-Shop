package com.sanryoo.shopping.feature.presentation.using.product

import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product

sealed class ProductUiEvent {
    object ClearFocus: ProductUiEvent()
    object BackToPrevScreen: ProductUiEvent()
    object NavigateToLogIn: ProductUiEvent()
    object NavigateToCart: ProductUiEvent()
    object NavigateToChats: ProductUiEvent()
    data class NavigateToMessage(val otherId: String): ProductUiEvent()
    data class CheckOut(val orders: List<Order>): ProductUiEvent()
    data class ViewShop(val uid: String): ProductUiEvent()
    data class ViewProduct(val product: Product): ProductUiEvent()
    data class ShowSnackBar(val message: String): ProductUiEvent()
    data class SetShowBottomSheet(val status: Boolean): ProductUiEvent()
}
