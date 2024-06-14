package com.sanryoo.shopping.feature.presentation.using.review

import android.net.Uri
import com.sanryoo.shopping.feature.domain.model.Order
import com.sanryoo.shopping.feature.domain.model.Product
import com.sanryoo.shopping.feature.domain.model.User

data class ReviewState(
    var order: Order = Order(),
    var rate: Int = 0,
    var comment: String = "",
    var listImagesComment: List<Uri> = emptyList(),

    var isLoading: Boolean = false
)
