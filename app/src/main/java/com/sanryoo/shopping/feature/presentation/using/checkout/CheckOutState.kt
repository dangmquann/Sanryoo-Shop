package com.sanryoo.shopping.feature.presentation.using.checkout

import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.User

data class CheckOutState(
    var loading: Boolean = false,
    var user: User = User(),
    var orders: List<Order> = emptyList()
)
