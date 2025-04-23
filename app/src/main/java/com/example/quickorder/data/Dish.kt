package com.example.quickorder.data

data class Dish(
    val name: String = "",
    val price: Int = 0
) {
    fun toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "price" to price
    )

    companion object {
        fun fromMap(map: Map<String, Any>): Dish {
            val name = map["name"] as? String ?: ""
            val price = (map["price"] as? Long)?.toInt() ?: 0
            return Dish(name, price)
        }
    }
}
