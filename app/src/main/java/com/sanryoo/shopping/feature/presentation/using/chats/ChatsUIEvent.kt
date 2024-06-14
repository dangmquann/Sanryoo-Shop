package com.sanryoo.shopping.feature.presentation.using.chats

import com.sanryoo.shopping.feature.presentation.using.message.MessageUIEvent
import com.sanryoo.shopping.feature.presentation.using.product.ProductUiEvent

sealed class ChatsUIEvent {
    object BackToPrevScreen : ChatsUIEvent()
    data class NavigateToMessage(val otherId: String): ChatsUIEvent()
}
