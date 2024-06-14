package com.sanryoo.shopping.feature.presentation.using.change_password

data class ChangePasswordState(
    var oldPassword: String = "",
    var newPassword: String = "",
    var confirmPassword: String = "",
    var visibleOldPassword: Boolean = false,
    var visibleNewPassword: Boolean = false,
    var visibleConfirmPassword: Boolean = false,
    var isLoading: Boolean = false,
)
