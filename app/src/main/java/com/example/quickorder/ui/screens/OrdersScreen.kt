package com.example.quickorder.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickorder.viewmodel.OrderViewModel
import com.example.quickorder.data.Order
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(
    navController: NavHostController,
    orderViewModel: OrderViewModel,
    isAdmin: Boolean
) {
    val orders by orderViewModel.orders.collectAsState()
    val isRefreshing by orderViewModel.refreshing.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    LaunchedEffect(Unit) {
        orderViewModel.loadOrders()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("📦 All Orders", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                orderViewModel.loadOrders()
            }
        ) {
            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No orders found.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(orders) { order ->
                        OrderCard(order = order, isAdmin = isAdmin, orderViewModel = orderViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, isAdmin: Boolean, orderViewModel: OrderViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(" Order #: ${order.shortOrderId}", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(4.dp))
            order.timestamp?.let { timestamp ->
                val date = timestamp.toDate()
                val formatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(date)
                Text(" Placed on: $formatted", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(" Items:", style = MaterialTheme.typography.labelMedium)
            if (order.items.isEmpty()) {
                Text("• No items found", style = MaterialTheme.typography.bodySmall)
            } else {
                order.items.forEach {
                    Text("• ${it.name} - ₹${it.price}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(" Total: ₹${order.totalAmount}", style = MaterialTheme.typography.bodyMedium)

            // 🟢 Status Badge
            StatusBadge(status = order.status)

            if (isAdmin) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        orderViewModel.updateOrderStatus(order.orderId, "Completed")
                    }) {
                        Text("Mark Completed")
                    }

                    Button(
                        onClick = {
                            orderViewModel.updateOrderStatus(order.orderId, "Cancelled")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            // Confirmation before deletion
                            orderViewModel.deleteOrder(order.orderId)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
@Composable
fun StatusBadge(status: String) {
    val color = when (status.lowercase()) {
        "completed" -> MaterialTheme.colorScheme.primary
        "cancelled" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.secondary
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Text(
            text = status,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
