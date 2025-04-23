package com.example.quickorder.viewmodel

import androidx.lifecycle.ViewModel
import com.example.quickorder.data.CartItem
import com.example.quickorder.data.Dish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> get() = _cartItems.asStateFlow()

    val totalAmount: StateFlow<Int> = MutableStateFlow(0)

    fun addToCart(dish: Dish) {
        val currentItems = _cartItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.dish.name == dish.name }

        if (existingIndex >= 0) {
            val item = currentItems[existingIndex]
            currentItems[existingIndex] = item.copy(quantity = item.quantity + 1)
        } else {
            currentItems.add(CartItem(dish = dish, quantity = 1))
        }

        _cartItems.value = currentItems
        updateTotal()
    }

    fun removeFromCart(dish: Dish) {
        val currentItems = _cartItems.value.toMutableList()
        val existingIndex = currentItems.indexOfFirst { it.dish.name == dish.name }

        if (existingIndex >= 0) {
            val item = currentItems[existingIndex]
            if (item.quantity > 1) {
                currentItems[existingIndex] = item.copy(quantity = item.quantity - 1)
            } else {
                currentItems.removeAt(existingIndex)
            }
        }

        _cartItems.value = currentItems
        updateTotal()
    }

    private fun updateTotal() {
        val total = _cartItems.value.sumOf { it.dish.price * it.quantity }
        (totalAmount as MutableStateFlow).value = total
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        (totalAmount as MutableStateFlow).value = 0
    }
}
