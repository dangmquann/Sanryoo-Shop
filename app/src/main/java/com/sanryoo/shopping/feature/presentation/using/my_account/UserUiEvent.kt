package com.sanryoo.shopping.feature.presentation.using.my_account

sealed class UserUiEvent {
    object BackToProfile: UserUiEvent()
    data class SetShowBottomSheet(val isShow: Boolean) : UserUiEvent()
}
