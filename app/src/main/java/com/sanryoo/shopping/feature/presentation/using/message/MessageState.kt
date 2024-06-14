package com.sanryoo.shopping.feature.presentation.using.message

import android.net.Uri
import com.sanryoo.shopping.feature.domain.model.Message
import com.sanryoo.shopping.feature.domain.model.User

data class MessageState(
    var user: User = User(),
    var others: User = User(),
    var messages: List<Message> = emptyList(),

    var text: String = "",
    var images: List<Uri> = emptyList(),

    var sending: Boolean = false
)
