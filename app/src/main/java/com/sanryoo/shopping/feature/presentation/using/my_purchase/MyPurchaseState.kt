package com.sanryoo.shopping.feature.presentation.using.my_purchase

import com.sanryoo.shopping.feature.domain.model.Order

data class MyPurchaseState(
    var oldTab: Int = 0,
    var currentTab: Int = 0,
    var allOrders: List<Order> = emptyList(),
    var orderedOrders: List<Order> = emptyList(),
    var shippingOrders: List<Order> = emptyList(),
    var shippedOrders: List<Order> = emptyList(),
    var cancelledOrders: List<Order> = emptyList(),
)