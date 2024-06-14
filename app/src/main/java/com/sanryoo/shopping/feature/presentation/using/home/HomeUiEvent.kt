package com.sanryoo.shopping.feature.presentation.using.home

import com.sanryoo.shopping.feature.domain.model.Product

sealed class HomeUiEvent {
    object NavigateToLogIn : HomeUiEvent()
    object NavigateToCart : HomeUiEvent()
    object NavigateToChats : HomeUiEvent()
    data class NavigateToProduct(val product: Product) : HomeUiEvent()
}
