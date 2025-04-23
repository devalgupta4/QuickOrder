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

@Composable
fun ManageOrdersScreen(
    navController: NavHostController,
    orderViewModel: OrderViewModel
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
        Text("🛠 Manage Orders", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { orderViewModel.loadOrders() }
        ) {
            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No orders found.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(orders) { order ->
                        AdminOrderCard(order = order, orderViewModel = orderViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(order: Order, orderViewModel: OrderViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(" Order #: ${order.shortOrderId}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Text(" Items:", style = MaterialTheme.typography.labelMedium)
            if (order.items.isEmpty()) {
                Text("• No items found", style = MaterialTheme.typography.bodySmall)
            } else {
                order.items.forEach {
                    Text("• ${it.name} - ₹${it.price}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(" Total: ₹${order.totalAmount}", style = MaterialTheme.typography.bodyMedium)
            Text(" Status: ${order.status}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    orderViewModel.updateOrderStatus(order.orderId, "Preparing")
                }) {
                    Text("Preparing")
                }
                Button(onClick = {
                    orderViewModel.updateOrderStatus(order.orderId, "Prepared")
                }) {
                    Text("Prepared")
                }
                Button(onClick = {
                    orderViewModel.updateOrderStatus(order.orderId, "Paid")
                }) {
                    Text("Paid")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    orderViewModel.deleteOrder(order.orderId)
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("🗑 Delete Order")
            }
        }
    }
}
