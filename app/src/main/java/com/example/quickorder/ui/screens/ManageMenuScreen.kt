package com.example.quickorder.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.quickorder.viewmodel.RestaurantViewModel

@Composable
fun ManageMenuScreen(navController: NavHostController, restaurantViewModel: RestaurantViewModel) {
    var dishName by remember { mutableStateOf("") }
    var dishPrice by remember { mutableStateOf("") }
    val context = LocalContext.current
    val dishes by restaurantViewModel.dishes.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Manage Menu", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = dishName,
            onValueChange = { dishName = it },
            label = { Text("Dish Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = dishPrice,
            onValueChange = { dishPrice = it },
            label = { Text("Dish Price") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val price = dishPrice.toIntOrNull()
                if (price != null && dishName.isNotBlank()) {
                    restaurantViewModel.addDish(dishName, price, {
                        Toast.makeText(context, "Dish Added!", Toast.LENGTH_SHORT).show()
                        dishName = ""  // Clear input field
                        dishPrice = "" // Clear input field
                    }, { message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() })
                } else {
                    Toast.makeText(context, "Enter valid name & price", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Dish")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Current Menu", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(dishes) { dish ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = dish.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = "₹${dish.price}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}
