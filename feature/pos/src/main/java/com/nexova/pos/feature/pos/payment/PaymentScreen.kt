package com.nexova.pos.feature.pos.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexova.pos.core.designsystem.components.NexovaButton
import com.nexova.pos.core.designsystem.components.NexovaCard
import com.nexova.pos.core.network.payment.PaymentResponse
import com.nexova.pos.core.network.payment.PaymentStatus

@Composable
fun PaymentScreen(
    paymentResponse: PaymentResponse?,
    onPaymentComplete: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (paymentResponse == null) {
            CircularProgressIndicator()
            Text("Menyiapkan Pembayaran...", modifier = Modifier.padding(top = 16.dp))
        } else {
            NexovaCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Bayar", style = MaterialTheme.typography.labelLarge)
                    Text("Rp ${paymentResponse.transactionId}", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    val qrCodeData = paymentResponse.qrCodeData
                    if (qrCodeData != null) {
                        // In a real app, use a QR library to render qrCodeData
                        Text("[QR CODE PLACEHOLDER]", style = MaterialTheme.typography.bodyLarge)
                        Text(qrCodeData, style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("Silakan selesaikan pembayaran di terminal", style = MaterialTheme.typography.bodyMedium)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (paymentResponse.status == PaymentStatus.PAID) {
                        Text("Pembayaran Berhasil!", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        NexovaButton(onClick = onPaymentComplete, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                            Text("Selesai")
                        }
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Text("Menunggu Konfirmasi...", modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}
