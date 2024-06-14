package com.sanryoo.shopping.feature.presentation.authentication.login

sealed class LogInUiEvent {
    object HideHeyBoard : LogInUiEvent()
    object NavigateToSignUp : LogInUiEvent()
    object BackToProfile : LogInUiEvent()
    data class ShowSnackBar(val message: String) : LogInUiEvent()
}
