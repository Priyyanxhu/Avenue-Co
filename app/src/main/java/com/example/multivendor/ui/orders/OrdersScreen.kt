package com.example.multivendor.ui.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.MarketplaceRepository
import com.example.multivendor.data.Order
import com.example.multivendor.theme.BrandOrange
import com.example.multivendor.theme.StarAmber
import com.example.multivendor.theme.SuccessGreen
import com.example.multivendor.ui.components.EmptyState
import com.example.multivendor.ui.components.OrderInvoiceDialog
import com.example.multivendor.ui.components.OrderTrackingStepper
import com.example.multivendor.ui.components.formatPrice
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val repository: MarketplaceRepository = AppContainer.repository
) : ViewModel() {
    val orders = repository.orders
    fun reorder(id: String, onDone: () -> Unit) {
        viewModelScope.launch { repository.reorder(id); onDone() }
    }
    fun cancelOrder(id: String, onDone: () -> Unit = {}) {
        viewModelScope.launch { repository.updateOrderStatus(id, "Cancelled"); onDone() }
    }
    fun requestReturn(id: String, onDone: () -> Unit = {}) {
        viewModelScope.launch { repository.updateOrderStatus(id, "Refunded"); onDone() }
    }
}

@Composable
fun OrdersScreen(
    onBrowse: () -> Unit,
    onReordered: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = viewModel { OrdersViewModel(AppContainer.repository) }
) {
    val orders by viewModel.orders.collectAsState()
    var selectedInvoiceOrder by remember { mutableStateOf<Order?>(null) }
    var selectedCancelOrder by remember { mutableStateOf<Order?>(null) }
    var selectedReturnOrder by remember { mutableStateOf<Order?>(null) }

    if (orders.isEmpty()) {
        EmptyState(
            title = "No orders yet",
            subtitle = "Your multi-vendor orders will appear here.",
            icon = Icons.Filled.ShoppingBag,
            actionLabel = "Start shopping",
            onAction = onBrowse,
            modifier = modifier
        )
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("My Orders (${orders.size}) 🛍️", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Real-time fulfillment tracking & GST invoices", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(orders, key = { it.id }) { order ->
            ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp)) {
                    // Header: ID & Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(order.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(order.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            color = when (order.status) {
                                "Confirmed" -> MaterialTheme.colorScheme.primaryContainer
                                "Preparing" -> StarAmber.copy(alpha = 0.2f)
                                "Shipped" -> BrandOrange.copy(alpha = 0.2f)
                                "Delivered" -> SuccessGreen.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                order.status,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                color = when (order.status) {
                                    "Confirmed" -> MaterialTheme.colorScheme.primary
                                    "Preparing" -> StarAmber
                                    "Shipped" -> BrandOrange
                                    "Delivered" -> SuccessGreen
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // 4-Stage Visual Order Stepper
                    OrderTrackingStepper(status = order.status)

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(10.dp))

                    // Products list in order
                    order.lines.forEach { line ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${line.quantity} × ${line.productName}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            Text(formatPrice(line.unitPrice * line.quantity), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatPrice(order.subtotal), style = MaterialTheme.typography.bodySmall)
                    }
                    if (order.discount > 0) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Discount (${order.promoCode ?: ""})", style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                            Text("-${formatPrice(order.discount)}", style = MaterialTheme.typography.bodySmall, color = SuccessGreen)
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Shipping", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(if (order.shipping == 0.0) "FREE" else formatPrice(order.shipping), style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount Paid", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(formatPrice(order.total), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(Modifier.height(4.dp))
                    Text("📍 Delivered to: ${order.address}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("💳 Paid via: ${order.paymentMethod}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(Modifier.height(12.dp))

                    // Action buttons: Invoice & Reorder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedInvoiceOrder = order },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Receipt, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.padding(2.dp))
                            Text("Tax Invoice")
                        }
                        Button(
                            onClick = { viewModel.reorder(order.id) { onReordered() } },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Repeat, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.padding(2.dp))
                            Text("Reorder")
                        }
                    }

                    // Cancel or Return Actions
                    if (order.status == "Confirmed" || order.status == "Preparing") {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { selectedCancelOrder = order },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = androidx.compose.ui.graphics.Color(0xFFDC2626))
                        ) {
                            Icon(Icons.Filled.Cancel, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.padding(2.dp))
                            Text("Cancel Order", fontWeight = FontWeight.Bold)
                        }
                    } else if (order.status == "Delivered") {
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { selectedReturnOrder = order },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = androidx.compose.ui.graphics.Color(0xFF2563EB))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.KeyboardReturn, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.padding(2.dp))
                            Text("Request Return / Exchange", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    selectedInvoiceOrder?.let { order ->
        OrderInvoiceDialog(order = order, onDismiss = { selectedInvoiceOrder = null })
    }

    selectedCancelOrder?.let { order ->
        CancelOrderDialog(
            order = order,
            onDismiss = { selectedCancelOrder = null },
            onConfirmCancel = {
                viewModel.cancelOrder(order.id)
                selectedCancelOrder = null
            }
        )
    }

    selectedReturnOrder?.let { order ->
        ReturnOrderDialog(
            order = order,
            onDismiss = { selectedReturnOrder = null },
            onConfirmReturn = {
                viewModel.requestReturn(order.id)
                selectedReturnOrder = null
            }
        )
    }
}

@Composable
fun CancelOrderDialog(
    order: Order,
    onDismiss: () -> Unit,
    onConfirmCancel: () -> Unit
) {
    val reasons = listOf(
        "Ordered wrong product / size",
        "Found lower price elsewhere",
        "Need to change delivery address",
        "Expected delivery date too late",
        "Other reasons"
    )
    var selectedReason by remember { mutableStateOf(reasons.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel Order ${order.id}?", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Please select a reason for cancellation. Any payments made will be refunded within 2-4 hours to your original payment method.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedReason == reason) Color(0xFF0F2557) else Color(0xFFF1F5F9),
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(Modifier.width(10.dp))
                        Text(
                            reason,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedReason == reason) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                Text("Confirm Cancellation")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Keep Order")
            }
        }
    )
}

@Composable
fun ReturnOrderDialog(
    order: Order,
    onDismiss: () -> Unit,
    onConfirmReturn: () -> Unit
) {
    val reasons = listOf(
        "Item defective / damaged on arrival",
        "Quality not as described",
        "Received incorrect item / variant",
        "Size or fit not suitable"
    )
    var selectedReason by remember { mutableStateOf(reasons.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Return Order ${order.id}?", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "A courier partner will pick up the package from ${order.address}. Your refund of ${formatPrice(order.total)} will be credited to Avenue Cash Wallet immediately upon verification.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedReason == reason) Color(0xFF2563EB) else Color(0xFFF1F5F9),
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(Modifier.width(10.dp))
                        Text(
                            reason,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selectedReason == reason) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirmReturn,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Submit Return Request")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
