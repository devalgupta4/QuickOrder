package com.example.quickorder.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickorder.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var upiId by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Checkbox(checked = isAdmin, onCheckedChange = { isAdmin = it })
            Text("Register as Admin")
        }

        if (isAdmin) {
            TextField(value = upiId, onValueChange = { upiId = it }, label = { Text("UPI ID") })
        }

        Button(onClick = {
            authViewModel.register(email, password, isAdmin, {
                if (isAdmin) {
                    navController.navigate("manage_menu")
                } else {
                    navController.navigate("home")
                }
            }, { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() })
        }) {
            Text("Register")
        }
    }
}
