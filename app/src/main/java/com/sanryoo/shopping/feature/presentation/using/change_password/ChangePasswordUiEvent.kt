package com.sanryoo.shopping.feature.presentation.using.change_password

sealed class ChangePasswordUiEvent {
    object HideKeyBoard: ChangePasswordUiEvent()
    object BackToProfile: ChangePasswordUiEvent()
    data class ShowSnackBar(val message: String): ChangePasswordUiEvent()
}
