package com.example.quickorder.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var isAdmin = mutableStateOf(false)
        private set

    fun register(
        email: String,
        password: String,
        isAdmin: Boolean,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    val userMap = mapOf("email" to email, "isAdmin" to isAdmin)
                    firestore.collection("users").document(uid).set(userMap)
                        .addOnSuccessListener {
                            this.isAdmin.value = isAdmin
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure("Failed to save user: ${e.message}")
                        }
                } else {
                    onFailure("Invalid user")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Registration failed")
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    firestore.collection("users").document(uid).get()
                        .addOnSuccessListener { doc ->
                            isAdmin.value = doc.getBoolean("isAdmin") == true
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure("Failed to fetch user role: ${e.message}")
                        }
                } else {
                    onFailure("Invalid user")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Login failed")
            }
    }

    fun logout() {
        auth.signOut()
        isAdmin.value = false
    }
    fun setAdmin(value: Boolean) {
        isAdmin.value = value
    }

}
