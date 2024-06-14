package com.sanryoo.shopping.feature.presentation.using.profile

sealed class ProfileUiEvent {
    object NavigateToLogIn : ProfileUiEvent()
    object NavigateToSignUp : ProfileUiEvent()
    object NavigateToCart : ProfileUiEvent()
    object NavigateToChats : ProfileUiEvent()
    object NavigateToUserScreen : ProfileUiEvent()
    object NavigateToMyPurchase : ProfileUiEvent()
    object NavigateToMyLikesScreen : ProfileUiEvent()
    object NavigateToPasswordScreen : ProfileUiEvent()
    object NavigateToShopScreen : ProfileUiEvent()
}
