package com.example.quickorder.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickorder.data.Dish
import com.example.quickorder.data.Order
import com.example.quickorder.viewmodel.OrderViewModel
import com.example.quickorder.viewmodel.RestaurantViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun HomeScreen(
    navController: NavHostController,
    restaurantViewModel: RestaurantViewModel,
    orderViewModel: OrderViewModel
) {
    val dishes by restaurantViewModel.dishes.collectAsState()
    val context = LocalContext.current
    val dishQuantities = remember { mutableStateMapOf<String, Int>() }

    val totalAmount = dishes.sumOf { dish ->
        (dishQuantities[dish.name] ?: 0) * dish.price
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Available Dishes",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(dishes) { dish ->
                val quantity = dishQuantities[dish.name] ?: 0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = dish.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "₹${dish.price}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (quantity > 0) {
                                    dishQuantities[dish.name] = quantity - 1
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = "Decrease Quantity",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                "$quantity",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            IconButton(onClick = {
                                dishQuantities[dish.name] = quantity + 1
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Increase Quantity",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        if (totalAmount > 0) {
            Column {
                Text(
                    text = "Total: ₹$totalAmount",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Button(
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        val restaurantId = FirebaseAuth.getInstance().currentUser?.email

                        if (userId.isNullOrBlank() || restaurantId.isNullOrBlank()) {
                            Toast.makeText(context, "User or restaurant ID missing", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val orderId = UUID.randomUUID().toString()
                        val items = dishes.flatMap { dish ->
                            val qty = dishQuantities[dish.name] ?: 0
                            List(qty) { Dish(dish.name, dish.price) }
                        }

                        val order = Order(
                            orderId = orderId,
                            userId = userId,
                            restaurantId = restaurantId,
                            status = "Unpaid",
                            totalAmount = totalAmount,
                            items = items
                        )

                        orderViewModel.placeOrder(
                            order,
                            onSuccess = {
                                Toast.makeText(context, "Order placed!", Toast.LENGTH_SHORT).show()
                                dishQuantities.clear()
                                navController.navigate("orders") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onFailure = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Place Order", color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(35.dp))
            }
        }
    }
}
