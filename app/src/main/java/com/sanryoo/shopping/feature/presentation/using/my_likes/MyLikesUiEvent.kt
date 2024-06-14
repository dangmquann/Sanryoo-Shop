package com.sanryoo.shopping.feature.presentation.using.my_likes

sealed class MyLikesUiEvent {
    object BackToPrevScreen: MyLikesUiEvent()
    data class ViewShop(val uid: String) : MyLikesUiEvent()
}
