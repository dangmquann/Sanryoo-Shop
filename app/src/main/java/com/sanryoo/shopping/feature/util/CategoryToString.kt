package com.sanryoo.shopping.feature.util

fun List<String>.toCategoryString(): String {
    var result = ""
    this.forEachIndexed { index, value ->
        result += value
        if (index != this.size - 1) {
            result += " > "
        }
    }
    return result
}