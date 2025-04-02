package com.example.quickorder.data

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val status: String = "Unpaid",
    val totalAmount: Int = 0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "userId" to userId,
            "status" to status,
            "totalAmount" to totalAmount
        )
    }
}
