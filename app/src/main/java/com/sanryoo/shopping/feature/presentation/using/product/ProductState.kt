package com.sanryoo.shopping.feature.presentation.using.product

import android.net.Uri
import androidx.compose.ui.geometry.Offset
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.User

data class ProductState(
    var product: Product = Product(),
    var productOfShop: List<Product> = emptyList(),
    var similarProducts: List<Product> = emptyList(),

    var user: User = User(),

    var addToCart: Order = Order(),

    var sheetContent: SheetContent = SheetContent.DEFAULT,

    var firstItemOffset: Offset = Offset.Zero,
    var numberOfCart: Int = 0,
    var numberOfChats: Int = 0
)

enum class SheetContent {
    DEFAULT, ADD_TO_CART, BUY_NOW
}
