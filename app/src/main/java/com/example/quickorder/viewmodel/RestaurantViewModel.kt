package com.example.quickorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickorder.data.Dish
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RestaurantViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _dishes = MutableStateFlow<List<Dish>>(emptyList())
    val dishes: StateFlow<List<Dish>> get() = _dishes

    init {
        loadDishes()
    }

    fun addDish(name: String, price: Int, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val dish = Dish(name, price)

        firestore.collection("dishes").add(dish.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception.message ?: "Failed to add dish") }
    }

    private fun loadDishes() {
        firestore.collection("dishes").addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener

            val dishList = snapshot?.documents?.map { doc ->
                Dish(
                    name = doc.getString("name") ?: "",
                    price = doc.getLong("price")?.toInt() ?: 0
                )
            } ?: emptyList()

            _dishes.value = dishList
        }
    }
}
