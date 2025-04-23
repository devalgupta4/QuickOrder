package com.example.quickorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.quickorder.navigation.SetupNavGraph

import com.example.quickorder.viewmodel.AuthViewModel
import com.example.quickorder.viewmodel.OrderViewModel
import com.example.quickorder.viewmodel.RestaurantViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickOrderTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val restaurantViewModel: RestaurantViewModel = viewModel()
                val orderViewModel: OrderViewModel = viewModel()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetupNavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        restaurantViewModel = restaurantViewModel,
                        orderViewModel = orderViewModel
                    )
                }
            }
        }
    }
}
