package com.example.quickorder.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickorder.utils.QRCodeGenerator

@Composable
fun QrPaymentScreen(navController: NavHostController, amount: Int, upiId: String) {
    val qrBitmap by remember { mutableStateOf(QRCodeGenerator.generateUPIQRCode(upiId, amount)) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Scan to Pay ₹$amount", style = MaterialTheme.typography.headlineSmall)

        qrBitmap?.let { bitmap ->
            // Convert Bitmap to ImageBitmap and display
            Image(bitmap = bitmap.asImageBitmap(), contentDescription = "UPI QR Code", modifier = Modifier.fillMaxWidth())
        }
    }
}
