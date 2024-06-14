package com.sanryoo.shopping.feature.presentation.using.my_shop

import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.User

data class MyShopState(
    var user: User = User(),
    var products: List<Product> = emptyList()
)