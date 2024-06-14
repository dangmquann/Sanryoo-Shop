package com.sanryoo.shopping.feature.presentation.authentication.signup

sealed class SignUpUiEvent {
    object BackToPrevScreen : SignUpUiEvent()
    object HideKeyBoard : SignUpUiEvent()
    data class ShowSnackBar(val message: String) : SignUpUiEvent()
}