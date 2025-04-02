package com.example.quickorder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickorder.viewmodel.OrderViewModel
import com.example.quickorder.data.Order // Ensure you import the correct Order model

@Composable
fun OrdersScreen(navController: NavHostController, orderViewModel: OrderViewModel) {
    val orders by orderViewModel.orders.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(orders) { order -> // Corrected item type
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Order ID: ${order.orderId}")
                    Text("Status: ${order.status}")
                    Text("Total: ₹${order.totalAmount}")

                    if (orderViewModel.isAdmin()) {
                        Row {
                            Button(onClick = { orderViewModel.updateOrderStatus(order.orderId, "Paid") }) {
                                Text("Mark Paid")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { orderViewModel.updateOrderStatus(order.orderId, "Preparing") }) {
                                Text("Mark Preparing")
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { orderViewModel.updateOrderStatus(order.orderId, "Prepared") }) {
                                Text("Mark Prepared")
                            }
                        }
                    }
                }
            }
        }
    }
}
