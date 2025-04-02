package com.example.quickorder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickorder.viewmodel.AuthViewModel

@Composable
fun HomeScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Welcome to QuickOrder", style = MaterialTheme.typography.headlineSmall)

        if (authViewModel.isAdmin()) {
            Button(onClick = { navController.navigate("manage_menu") }) {
                Text("Manage Menu")
            }
            Button(onClick = { navController.navigate("orders") }) {
                Text("View Orders")
            }
        } else {
            Button(onClick = { navController.navigate("orders") }) {
                Text("View My Orders")
            }
        }

        Button(onClick = { authViewModel.logout(); navController.navigate("login") }) {
            Text("Logout")
        }
    }
}
