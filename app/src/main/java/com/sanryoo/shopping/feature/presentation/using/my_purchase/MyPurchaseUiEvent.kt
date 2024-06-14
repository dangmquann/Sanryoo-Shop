package com.sanryoo.shopping.feature.presentation.using.my_purchase

import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product

sealed class MyPurchaseUiEvent {
    object BackToPrevScreen: MyPurchaseUiEvent()
    data class ViewProduct(val product: Product): MyPurchaseUiEvent()
    data class CheckOut(val orders: List<Order>): MyPurchaseUiEvent()
    data class Review(val order: Order): MyPurchaseUiEvent()
}
