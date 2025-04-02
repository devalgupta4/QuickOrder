package com.example.quickorder.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun registerRestaurant(name: String, upiId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val restaurantData = hashMapOf("name" to name, "upiId" to upiId)

        firestore.collection("restaurants").document(userId)
            .set(restaurantData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception.message ?: "Error") }
    }

    fun addDishToMenu(name: String, price: Int, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val dish = hashMapOf("name" to name, "price" to price)

        firestore.collection("restaurants").document(userId)
            .collection("menu").add(dish)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception.message ?: "Error") }
    }
}
