package com.example.quickorder.data

data class Dish(
    val name: String = "",
    val price: Int = 0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "price" to price
        )
    }
}
