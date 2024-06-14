package com.sanryoo.shopping.feature.presentation.using.my_likes

import com.sanryoo.shopping.feature.domain.model.Like
import com.sanryoo.shopping.feature.domain.model.User

data class MyLikesState(
    var likes: List<Like> = emptyList(),
)
