package com.sanryoo.shopping.feature.presentation.using.chats

import com.sanryoo.shopping.feature.domain.model.Message
import com.sanryoo.shopping.feature.domain.model.User

data class ChatsState(
    var user: User = User(),
    var messages: List<Message> = emptyList()
)