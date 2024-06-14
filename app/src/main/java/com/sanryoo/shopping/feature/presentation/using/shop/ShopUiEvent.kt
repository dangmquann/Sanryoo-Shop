package com.sanryoo.shopping.feature.presentation.using.shop

import com.sanryoo.shopping.feature.domain.model.Product

sealed class ShopUiEvent {
    object BackToPrevScreen : ShopUiEvent()
    object NavigateToLogIn : ShopUiEvent()
    object NavigateToCart : ShopUiEvent()
    object NavigateToChats : ShopUiEvent()
    data class ViewProduct(val product: Product) : ShopUiEvent()
    data class NavigateToMessage(val otherId: String): ShopUiEvent()
}
