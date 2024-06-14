package com.sanryoo.shopping.feature.presentation.using.edit_product

import android.net.Uri
import com.sanryoo.shopping.feature.domain.model.Category
import com.sanryoo.shopping.feature.domain.model.Product

data class EditProductState(
    var images: List<Uri> = emptyList(),

    var product: Product = Product(),
    var isLoading: Boolean = false,

    var categories: List<Category> = emptyList(),
    var currentCategory: Category = Category(),

    var sheetContent: SheetContent = SheetContent.DEFAULT
)

enum class SheetContent {
    DEFAULT, CATEGORY, VARIATIONS, STOCK, IMAGES
}
