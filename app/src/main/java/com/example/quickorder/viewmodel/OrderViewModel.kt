package com.example.quickorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickorder.data.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> get() =_orders.asStateFlow()

    init {
        loadOrders()
    }

    fun placeOrder(order: Order, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("orders").add(order.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception.message ?: "Failed to place order") }
    }

    fun loadOrders() {
        firestore.collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val orderList = snapshot?.documents?.map { doc ->
                    Order(
                        orderId = doc.id,
                        userId = doc.getString("userId") ?: "",
                        status = doc.getString("status") ?: "Unpaid",
                        totalAmount = doc.getLong("totalAmount")?.toInt() ?: 0
                    )
                } ?: emptyList()
                _orders.value = orderList
            }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        firestore.collection("orders").document(orderId)
            .update("status", status)
            .addOnSuccessListener { loadOrders() }
    }

    fun isAdmin(): Boolean {
        return auth.currentUser?.email?.endsWith("@admin.com") == true
    }
    fun getOrderSummary(): String {
        return _orders.value.joinToString("\n") { order ->
            "Order ID: ${order.orderId}\nStatus: ${order.status}\nTotal: ₹${order.totalAmount}\n"
        }
    }

}
