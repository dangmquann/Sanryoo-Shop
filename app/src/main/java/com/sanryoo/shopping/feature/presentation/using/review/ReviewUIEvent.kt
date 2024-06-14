package com.sanryoo.shopping.feature.presentation.using.review

sealed class ReviewUIEvent {
    object BackToPrevScreen : ReviewUIEvent()
    object ClearFocus: ReviewUIEvent()
    data class ShowSnackBar(val message: String): ReviewUIEvent()
    data class SetShowBottomSheet(val status: Boolean) : ReviewUIEvent()

}
