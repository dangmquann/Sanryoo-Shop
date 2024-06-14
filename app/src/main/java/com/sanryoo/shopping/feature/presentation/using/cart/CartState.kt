package com.sanryoo.shopping.feature.presentation.using.cart

import com.sanryoo.shopping.feature.domain.model.Order

data class CartState(
    var orders: List<CartOrder> = emptyList(),
    var editOrder: Order = Order()
)

data class CartOrder(
    var order: Order = Order(),
    var checked: Boolean = false
)
