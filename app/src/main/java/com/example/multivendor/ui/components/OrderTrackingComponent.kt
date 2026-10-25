package com.example.multivendor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.multivendor.data.Order
import com.example.multivendor.theme.SuccessGreen

@Composable
fun OrderTrackingStepper(
    status: String,
    modifier: Modifier = Modifier
) {
    if (status.equals("Cancelled", ignoreCase = true)) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFEE2E2),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🚫 ", fontSize = 15.sp)
                Column {
                    Text("Order Cancelled", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFFDC2626))
                    Text("Full refund initiated to original payment method.", fontSize = 11.sp, color = Color(0xFF991B1B))
                }
            }
        }
        return
    }

    if (status.equals("Refunded", ignoreCase = true)) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFEFF6FF),
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("💳 ", fontSize = 15.sp)
                Column {
                    Text("Return Verified & Refunded", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = Color(0xFF1E3A8A))
                    Text("Amount credited to your Avenue Cash Wallet.", fontSize = 11.sp, color = Color(0xFF1E40AF))
                }
            }
        }
        return
    }

    val steps = listOf("Confirmed", "Preparing", "Shipped", "Delivered")
    val currentIndex = when (status.lowercase()) {
        "confirmed" -> 0
        "preparing" -> 1
        "shipped" -> 2
        "delivered" -> 3
        else -> 0
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, step ->
                val isDone = index <= currentIndex
                val isCurrent = index == currentIndex

                // Milestone circle
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isDone) SuccessGreen else MaterialTheme.colorScheme.outlineVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }

                // Connecting line
                if (index < steps.size - 1) {
                    val lineDone = index < currentIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(if (lineDone) SuccessGreen else MaterialTheme.colorScheme.outlineVariant)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, step ->
                val isCurrent = index == currentIndex
                val isDone = index <= currentIndex
                Text(
                    text = when (step) {
                        "Confirmed" -> "Confirmed"
                        "Preparing" -> "Packed"
                        "Shipped" -> "Dispatched"
                        "Delivered" -> "Delivered"
                        else -> step
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isDone) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OrderInvoiceDialog(
    order: Order,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tax Invoice / Receipt 🧾", fontWeight = FontWeight.Bold)
                    Text(order.id, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                Icon(Icons.Filled.Receipt, null, tint = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Date: ${order.date}", style = MaterialTheme.typography.bodySmall)
                Text("Deliver To: ${order.address}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Payment: ${order.paymentMethod}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)

                Spacer(Modifier.height(6.dp))
                HorizontalDivider()
                Spacer(Modifier.height(6.dp))

                Text("Items Ordered:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                order.lines.forEach { line ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${line.quantity}x ${line.productName}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            formatPrice(line.unitPrice * line.quantity),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(6.dp))
                HorizontalDivider()
                Spacer(Modifier.height(6.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Item Subtotal:", style = MaterialTheme.typography.bodySmall)
                    Text(formatPrice(order.subtotal), style = MaterialTheme.typography.bodySmall)
                }
                if (order.discount > 0) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Promo Discount (${order.promoCode ?: ""}):", style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                        Text("-${formatPrice(order.discount)}", style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Shipping Charges:", style = MaterialTheme.typography.bodySmall)
                    Text(if (order.shipping == 0.0) "FREE" else formatPrice(order.shipping), style = MaterialTheme.typography.bodySmall)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GST (18% included):", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatPrice(order.total * 0.18 / 1.18), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Grand Total Paid:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(formatPrice(order.total), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Icon(Icons.Filled.Download, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Download PDF")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
