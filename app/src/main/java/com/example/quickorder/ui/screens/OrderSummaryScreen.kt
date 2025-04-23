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

@Composable
fun OrderSummaryScreen(
    navController: NavHostController,
    orderViewModel: OrderViewModel,
    restaurantId: String
) {
    val cartState by orderViewModel.cart.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Order Summary", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(cartState.items) { item ->
                Text("${item.dish.name} x ${item.quantity} = ₹${item.dish.price * item.quantity}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Total: ₹${cartState.totalAmount()}")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                orderViewModel.placeOrder(restaurantId)
                navController.navigate("orders")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place Order")
        }
    }
}
