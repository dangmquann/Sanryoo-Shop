package com.sanryoo.shopping.feature.presentation.using.profile

data class ProfileState(
    var isLogged: Boolean = false,
    var email: String = "",
    var displayName: String = "",
    var photoUrl: String = "",
    var isEmailAuth: Boolean = false,

    var numberOfCart: Int = 0,
    var numberOfChats: Int = 0
)
