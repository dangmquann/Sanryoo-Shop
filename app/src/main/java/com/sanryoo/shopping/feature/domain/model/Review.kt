package com.sanryoo.shopping.feature.domain.model

import java.util.Date

data class Review(
    var user: User = User(),
    var time: Date = Date(),
    var rate: Int = 0,
    var comment: String = "",
    var images: List<String> = emptyList()
)
