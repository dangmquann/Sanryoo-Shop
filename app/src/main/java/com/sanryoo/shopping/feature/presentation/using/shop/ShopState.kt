package com.sanryoo.shopping.feature.presentation.using.shop

import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.User

data class ShopState(
    var user: User = User(),
    var shop: User = User(),
    var products: List<Product> = emptyList(),
    var numberOfLikes: Long = 0L,
    var liked: Boolean = false,

    var numberOfCart: Int = 0,
    var numberOfChats: Int = 0
)
