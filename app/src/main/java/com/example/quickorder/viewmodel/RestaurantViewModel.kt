package com.example.quickorder.viewmodel

import androidx.lifecycle.ViewModel
import com.example.quickorder.data.Dish
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RestaurantViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _dishes = MutableStateFlow<List<Dish>>(emptyList())
    val dishes: StateFlow<List<Dish>> get() = _dishes

    init {
        loadDishes()
    }

    fun addDish(
        name: String,
        price: Int,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onFailure("User not logged in")
            return
        }

        val dish = Dish(name, price)

        firestore.collection("restaurants")
            .document(userId)
            .collection("menu")
            .add(dish.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to add dish")
            }
    }

    fun loadDishes() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("restaurants")
            .document(userId)
            .collection("menu")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _dishes.value = emptyList() // Clear on error
                    return@addSnapshotListener
                }

                val dishList = snapshot?.documents?.mapNotNull { doc ->
                    val name = doc.getString("name")
                    val price = doc.getLong("price")?.toInt()
                    if (name != null && price != null) {
                        Dish(name, price)
                    } else null
                } ?: emptyList()

                _dishes.value = dishList
            }
    }
}
