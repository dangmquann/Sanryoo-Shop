package com.sanryoo.shopping.feature.presentation.using.my_shop

import com.sanryoo.shopping.feature.domain.model.Product

sealed class MyShopUiEvent {
    object BackToProfile : MyShopUiEvent()
    object AddProduct : MyShopUiEvent()
    object NavigateToMyShopPurchases : MyShopUiEvent()
    data class EditProduct(val product: Product): MyShopUiEvent()
    data class ShowSnackBar(val message: String): MyShopUiEvent()
}
