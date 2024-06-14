package com.sanryoo.shopping.feature.presentation.authentication.login

data class LogInState(
    var email: String = "",
    var password: String = "",
    var visiblePassword: Boolean = false,
    var loadingButton: Boolean = false,
    var loadingScreen: Boolean = false,
)
