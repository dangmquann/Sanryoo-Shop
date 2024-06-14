package com.sanryoo.shopping.feature.domain.model

import java.util.Date

data class Order(
    var oid: String = "",
    var customer: User = User(),
    var product: Product = Product(),
    var variations: Map<String, String> = emptyMap(),
    var quantity: Long = 1L,
    var status: String = "",
    var message: String = "",
    var orderedDate: Date? = null,
    var confirmedDate: Date? = null,
    var shippedDate: Date? = null,
    var cancelledDate: Date? = null,

    var reviewed: Boolean = false
)

fun Order.getTotalCost(): Long {
    return this.product.price * this.quantity
}
