package com.sanryoo.shopping.feature.domain.model

data class Variation(
    var name: String = "",
    var child: List<String> = emptyList()
)
