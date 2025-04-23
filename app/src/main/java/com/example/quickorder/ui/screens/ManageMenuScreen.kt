package com.example.quickorder.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Money
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.quickorder.viewmodel.RestaurantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMenuScreen(navController: NavHostController, restaurantViewModel: RestaurantViewModel) {
    var dishName by remember { mutableStateOf("") }
    var dishPrice by remember { mutableStateOf("") }
    val context = LocalContext.current
    val dishes by restaurantViewModel.dishes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Menu") },
                actions = {
                    TextButton(onClick = { navController.navigate("orders") }) {
                        Text("View Orders", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = dishName,
                        onValueChange = { dishName = it },
                        label = { Text("Dish Name") },
                        leadingIcon = { Icon(Icons.Default.Fastfood, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = dishPrice,
                        onValueChange = { dishPrice = it },
                        label = { Text("Dish Price") },
                        leadingIcon = { Icon(Icons.Default.Money, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val price = dishPrice.toIntOrNull()
                            if (price != null && dishName.isNotBlank()) {
                                restaurantViewModel.addDish(dishName, price, {
                                    Toast.makeText(context, "Dish Added!", Toast.LENGTH_SHORT).show()
                                    dishName = ""
                                    dishPrice = ""
                                }, { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                })
                            } else {
                                Toast.makeText(context, "Enter valid name & price", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Dish")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Current Menu",
                style = MaterialTheme.typography.headlineSmall
            )

            // 👇 Added prominent Go to Orders button here
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate("manage_orders") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Orders")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(dishes) { dish ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = dish.name, style = MaterialTheme.typography.titleMedium)
                                Text(text = "₹${dish.price}", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}
