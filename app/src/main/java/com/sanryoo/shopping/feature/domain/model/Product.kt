package com.sanryoo.shopping.feature.domain.model

data class Product(
    var pid: String = "",
    var user: User = User(),

    var images: List<String> = emptyList(),
    var name: String = "",
    var description: String = "",

    var category: List<String> = emptyList(),
    var variations: List<Variation> = listOf(
        Variation("Color", listOf("Black", "White")),
        Variation("Size", listOf("S", "M", "L"))
    ),
    var stocks: List<Stock> = listOf(
        Stock(listOf("Black", "S")),
        Stock(listOf("Black", "M")),
        Stock(listOf("Black", "L")),
        Stock(listOf("White", "S")),
        Stock(listOf("White", "M")),
        Stock(listOf("White", "L"))
    ),
    var price: Long = 0,

    var reviews: List<Review> = emptyList(),
    var sold: Long = 0
)

fun Product.getAvgRate() : Float {
    var sum = 0
    var avgRate = 0f
    if (this.reviews.isNotEmpty()) {
        this.reviews.forEach {
            sum += it.rate
        }
        avgRate = sum.toFloat() / this.reviews.size
    }
    return avgRate
}

fun Product.getSold() : String {
    return if (this.sold >= 1000000) {
        String.format("%.1fm", this.sold / 1000000f)
    } else if (this.sold >= 1000) {
        String.format("%.1fk", this.sold / 1000f)
    } else {
        this.sold.toString()
    }
}