package com.example.quickorder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quickorder.ui.screens.*
import com.example.quickorder.viewmodel.AuthViewModel
import com.example.quickorder.viewmodel.OrderViewModel
import com.example.quickorder.viewmodel.RestaurantViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    restaurantViewModel: RestaurantViewModel,  // Add this
    orderViewModel: OrderViewModel            // Add this
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, authViewModel) }
        composable("register") { RegisterScreen(navController, authViewModel) }
        composable("home") { HomeScreen(navController, authViewModel) }
        composable("manage_menu") { ManageMenuScreen(navController, restaurantViewModel) }  // Pass restaurantViewModel
        composable("orders") { OrdersScreen(navController, orderViewModel) }  // Pass orderViewModel
    }
}

