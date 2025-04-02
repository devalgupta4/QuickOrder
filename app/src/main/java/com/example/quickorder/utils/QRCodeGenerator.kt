package com.example.quickorder.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object QRCodeGenerator {
    fun generateUPIQRCode(upiId: String, amount: Int): Bitmap? {
        val upiUri = Uri.parse("upi://pay?pa=$upiId&pn=QuickOrder&am=$amount&cu=INR").toString()
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(upiUri, BarcodeFormat.QR_CODE, 500, 500)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }
}
