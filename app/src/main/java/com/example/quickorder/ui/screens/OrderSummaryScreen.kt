package com.example.quickorder.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickorder.viewmodel.OrderViewModel

@Composable
fun OrderSummaryScreen(navController: NavHostController, orderViewModel: OrderViewModel) {
    val context = LocalContext.current
    val orderSummary by remember { mutableStateOf(orderViewModel.getOrderSummary()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = orderSummary, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val upiUri = Uri.parse("upi://pay?pa=merchant@upi&pn=QuickOrder&mc=1234&tid=123456789&tr=123456&tn=Order%20Payment&am=50&cu=INR")
            val intent = Intent(Intent.ACTION_VIEW, upiUri)
            context.startActivity(intent)
        }) {
            Text("Pay with UPI")
        }
    }
}
