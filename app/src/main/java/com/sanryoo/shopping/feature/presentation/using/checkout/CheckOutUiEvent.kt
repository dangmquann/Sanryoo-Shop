package com.sanryoo.shopping.feature.presentation.using.checkout

sealed class CheckOutUiEvent {
    object ClearFocus : CheckOutUiEvent()
    object BackToPrevScreen : CheckOutUiEvent()
    data class ShowSnackBar(val message: String) : CheckOutUiEvent()
}
