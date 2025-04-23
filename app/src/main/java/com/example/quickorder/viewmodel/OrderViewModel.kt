package com.example.quickorder.viewmodel

import androidx.lifecycle.ViewModel
import com.example.quickorder.data.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> get() = _orders.asStateFlow()

    private val _latestOrder = MutableStateFlow<Order?>(null)
    val latestOrder: StateFlow<Order?> get() = _latestOrder.asStateFlow()

    private val _cart = MutableStateFlow(CartState())
    val cart: StateFlow<CartState> = _cart.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> get() = _refreshing.asStateFlow()

    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    private fun getRestaurantId(): String? = auth.currentUser?.email

    fun isAdmin(): Boolean {
        return getRestaurantId()?.endsWith("@admin.com") == true
    }

    fun addToCart(dish: Dish) {
        val currentItems = _cart.value.items.toMutableList()
        val index = currentItems.indexOfFirst { it.dish.name == dish.name }
        if (index >= 0) {
            val existing = currentItems[index]
            currentItems[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentItems.add(CartItem(dish, 1))
        }
        _cart.value = CartState(currentItems)
    }

    fun clearCart() {
        _cart.value = CartState()
    }

    fun placeOrder(restaurantId: String) {
        val userId = getCurrentUserId() ?: return
        val cartState = _cart.value
        val order = cartState.toOrder(userId, restaurantId)
        placeOrder(order, onSuccess = { clearCart() }, onFailure = { println(it) })
    }

    fun placeOrder(order: Order, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = getCurrentUserId() ?: return onFailure("User not authenticated")
        val restaurantId = getRestaurantId()
        val shortOrderId = (1000..9999).random().toString()

        if (restaurantId.isNullOrBlank()) {
            onFailure("Restaurant ID (email) is missing")
            return
        }

        val orderMap = order.toMap().toMutableMap().apply {
            put("userId", userId)
            put("shortOrderId", shortOrderId)
            put("restaurantId", restaurantId)
            put("timestamp", Timestamp.now())
        }

        firestore.collection("restaurants")
            .document(restaurantId)
            .collection("orders")
            .add(orderMap)
            .addOnSuccessListener {
                onSuccess()
                loadLatestOrderFromRestaurant(userId, restaurantId)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to place order")
            }
    }

    fun loadLatestOrderFromRestaurant(userId: String, restaurantId: String) {
        firestore.collection("restaurants")
            .document(restaurantId)
            .collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                val doc = result.documents.firstOrNull()
                _latestOrder.value = doc?.let { parseOrderFromDoc(it) }
            }
            .addOnFailureListener {
                println(" Failed to load latest order: ${it.message}")
            }
    }

    fun loadOrders() {
        if (_refreshing.value) return

        _refreshing.value = true

        firestore.collectionGroup("orders")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println(" Error loading orders: ${error.message}")
                    _refreshing.value = false
                    return@addSnapshotListener
                }

                val ordersList = snapshot?.documents
                    ?.mapNotNull { parseOrderFromDoc(it) }
                    ?.filter { order ->
                        order.items.isNotEmpty() && order.orderId.isNotBlank()
                    } ?: emptyList()


                _orders.value = ordersList
                _refreshing.value = false
            }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        val restaurantId = getRestaurantId()
        if (restaurantId.isNullOrBlank()) {
            println(" Cannot update order: restaurantId is missing")
            return
        }

        firestore.collection("restaurants")
            .document(restaurantId)
            .collection("orders")
            .document(orderId)
            .update("status", status)
            .addOnSuccessListener {
                println(" Status updated to $status")
                loadOrders()
            }
            .addOnFailureListener {
                println(" Failed to update status: ${it.message}")
            }
    }

    fun deleteOrder(orderId: String) {
        val restaurantId = getRestaurantId()
        if (restaurantId.isNullOrBlank()) {
            println(" Cannot delete order: restaurantId is missing")
            return
        }

        firestore.collection("restaurants")
            .document(restaurantId)
            .collection("orders")
            .document(orderId)
            .delete()
            .addOnSuccessListener {
                println("🗑 Order deleted: $orderId")
                loadOrders()
            }
            .addOnFailureListener {
                println(" Failed to delete order: ${it.message}")
            }
    }

    fun getOrderSummary(): String {
        return _orders.value.joinToString("\n") { order ->
            "Order ID: ${order.orderId}\nStatus: ${order.status}\nTotal: ₹${order.totalAmount}\n"
        }
    }

    private fun parseOrderFromDoc(doc: com.google.firebase.firestore.DocumentSnapshot): Order? {
        return try {
            val itemsRaw = doc["items"] as? List<*> ?: emptyList<Any>()
            val dishList = itemsRaw.mapNotNull { raw ->
                if (raw is Map<*, *>) {
                    val name = raw["name"] as? String
                    val price = when (val p = raw["price"]) {
                        is Long -> p.toInt()
                        is Int -> p
                        else -> null
                    }
                    if (name != null && price != null) Dish(name, price) else null
                } else null
            }

            Order(
                orderId = doc.id,
                userId = doc.getString("userId") ?: "",
                restaurantId = doc.getString("restaurantId") ?: "",
                shortOrderId = doc.getString("shortOrderId") ?: "",
                status = doc.getString("status") ?: "Unpaid",
                totalAmount = doc.getLong("totalAmount")?.toInt() ?: 0,
                items = dishList,
                timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
            )
        } catch (e: Exception) {
            println("❌ Failed to parse order: ${e.message}")
            null
        }
    }
}
