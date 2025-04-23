package com.example.quickorder.data

import com.google.firebase.Timestamp

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val shortOrderId: String = "", // 👈 short, human-friendly ID
    val restaurantId: String = "",
    val status: String = "",
    val totalAmount: Int = 0,
    val items: List<Dish> = emptyList(),
    val timestamp: Timestamp? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "shortOrderId" to shortOrderId,
            "restaurantId" to restaurantId,
            "status" to status,
            "totalAmount" to totalAmount,
            "items" to items.map { dish ->
                mapOf(
                    "name" to dish.name,
                    "price" to dish.price
                )
            },
            "timestamp" to (timestamp ?: Timestamp.now())
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): Order? {
            val orderId = map["orderId"] as? String ?: ""
            val userId = map["userId"] as? String ?: return null
            val shortOrderId = map["shortOrderId"] as? String ?: ""
            val restaurantId = map["restaurantId"] as? String ?: return null
            val status = map["status"] as? String ?: "Unpaid"
            val totalAmount = (map["totalAmount"] as? Long)?.toInt() ?: 0
            val timestamp = map["timestamp"] as? Timestamp

            val itemsRaw = map["items"] as? List<Map<String, Any>> ?: emptyList()
            val items = itemsRaw.mapNotNull { itemMap ->
                val name = itemMap["name"] as? String
                val price = (itemMap["price"] as? Long)?.toInt()
                if (name != null && price != null) Dish(name, price) else null
            }

            return Order(orderId, userId, shortOrderId, restaurantId, status, totalAmount, items, timestamp)
        }
    }
}
