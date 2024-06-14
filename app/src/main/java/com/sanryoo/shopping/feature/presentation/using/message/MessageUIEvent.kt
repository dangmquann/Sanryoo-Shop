package com.sanryoo.shopping.feature.presentation.using.message

sealed class MessageUIEvent {
    object ClearFocus : MessageUIEvent()
    object BackToPrevScreen : MessageUIEvent()
    object ScrollToFirstItem : MessageUIEvent()
}
