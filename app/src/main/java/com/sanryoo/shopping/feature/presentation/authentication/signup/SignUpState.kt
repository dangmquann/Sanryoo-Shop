package com.sanryoo.shopping.feature.presentation.authentication.signup

data class SignUpState(
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",
    var visiblePassword: Boolean = false,
    var visibleConfirmPassword: Boolean = false,
    var isLoading: Boolean = false
)