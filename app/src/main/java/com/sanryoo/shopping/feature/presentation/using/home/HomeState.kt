package com.sanryoo.shopping.feature.presentation.using.home

import com.sanryoo.shopping.feature.domain.model.Product

data class HomeState(
    var eventImages: List<String> = emptyList(),

    var categories: List<String> = listOf("All"),
    var currentCategory: String = "All",

    var allProduct: List<Product> = emptyList(),
    var showingProduct: List<Product> = emptyList(),

    var searchText: String = "",
    var searchResult: List<Product> = emptyList(),
    var numberOfCart: Int = 0,
    var numberOfChats: Int = 0,

)

data class EventImages(
    var images: List<String> = emptyList()
)