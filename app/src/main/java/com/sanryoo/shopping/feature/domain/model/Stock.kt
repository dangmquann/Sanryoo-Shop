package com.sanryoo.shopping.feature.domain.model

data class Stock(
    var variations: List<String> = emptyList(),
    var quantity: Long = 0
)
