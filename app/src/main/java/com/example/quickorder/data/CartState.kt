package com.example.quickorder.data

data class CartState(
    val items: List<CartItem> = emptyList()
) {
    fun totalAmount(): Int {
        return items.sumOf { it.dish.price * it.quantity }
    }

    fun toOrder(userId: String, restaurantId: String): Order {
        val orderItems = items.flatMap { cartItem ->
            List(cartItem.quantity) { cartItem.dish }
        }

        return Order(
            userId = userId,
            restaurantId = restaurantId,
            items = orderItems,
            totalAmount = totalAmount()
        )
    }
}
