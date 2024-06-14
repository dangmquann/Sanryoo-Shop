package com.sanryoo.shopping.feature.presentation.using.edit_product

sealed class EditProductUiEvent {
    object BackToShop : EditProductUiEvent()
    object ClearFocus : EditProductUiEvent()
    data class ShowSnackBar(val message: String): EditProductUiEvent()
    data class SetShowBottomSheet(val status: Boolean): EditProductUiEvent()
}
