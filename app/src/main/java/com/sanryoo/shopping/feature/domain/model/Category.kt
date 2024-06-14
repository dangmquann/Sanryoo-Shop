package com.sanryoo.shopping.feature.domain.model

data class Category(
    var name: String = "",
    var child: List<String> = emptyList()
)