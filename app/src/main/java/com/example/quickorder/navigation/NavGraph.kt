package com.example.quickorder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.quickorder.ui.screens.*
import com.example.quickorder.viewmodel.AuthViewModel
import com.example.quickorder.viewmodel.OrderViewModel
import com.example.quickorder.viewmodel.RestaurantViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    restaurantViewModel: RestaurantViewModel,
    orderViewModel: OrderViewModel
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController, authViewModel)
        }
        composable("register") {
            RegisterScreen(navController, authViewModel)
        }
        composable("home") {
            HomeScreen(navController, restaurantViewModel, orderViewModel)
        }
        composable("manage_menu") {
            ManageMenuScreen(navController, restaurantViewModel)
        }
        composable("orders") {
            OrdersScreen(
                navController = navController,
                orderViewModel = orderViewModel,
                isAdmin = authViewModel.isAdmin.value // <-- Pass isAdmin from AuthViewModel
            )
        }
        composable(
            "order_summary/{restaurantId}",
            arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            OrderSummaryScreen(navController, orderViewModel, restaurantId)
        }
        composable("manage_orders") {
            ManageOrdersScreen(navController, orderViewModel)
        }

    }
}
