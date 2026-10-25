package com.example.multivendor.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.MarketplaceRepository
import com.example.multivendor.data.Order
import com.example.multivendor.theme.BrandOrange
import com.example.multivendor.theme.StarAmber
import com.example.multivendor.theme.SuccessGreen
import com.example.multivendor.ui.components.EmptyState
import com.example.multivendor.ui.components.formatPrice
import kotlinx.coroutines.launch

private val OrderStatusFilters = listOf("All", "Confirmed", "Preparing", "Shipped", "Delivered")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrdersScreen(
    vendorId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    repository: MarketplaceRepository = AppContainer.repository
) {
    val orders by repository.ordersForVendor(vendorId).collectAsState(initial = emptyList())
    val earnings by repository.vendorEarnings(vendorId).collectAsState(initial = 0.0)
    val vendor = remember(vendorId) { repository.getVendor(vendorId) }
    val scope = rememberCoroutineScope()

    var selectedFilter by remember { mutableStateOf("All") }

    val filteredOrders = if (selectedFilter == "All") {
        orders
    } else {
        orders.filter { it.status.equals(selectedFilter, ignoreCase = true) }
    }

    val pendingCount = orders.count { it.status == "Confirmed" || it.status == "Preparing" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Order Fulfillment 📦", fontWeight = FontWeight.Bold)
                        Text(
                            "${vendor?.name ?: "Store"} • $pendingCount action required",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Metrics Banner
            item {
                ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Store Revenue", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formatPrice(earnings), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "${orders.size} Orders Total",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Filter Tabs
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(OrderStatusFilters) { filter ->
                        val count = if (filter == "All") orders.size else orders.count { it.status.equals(filter, ignoreCase = true) }
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text("$filter ($count)") },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Orders List
            if (filteredOrders.isEmpty()) {
                item {
                    EmptyState(
                        title = "No orders found",
                        subtitle = if (selectedFilter == "All") "No orders have been placed for this store yet." else "No orders with status '$selectedFilter'.",
                        icon = Icons.AutoMirrored.Filled.ReceiptLong
                    )
                }
            } else {
                items(filteredOrders, key = { it.id }) { order ->
                    val vendorLines = order.lines.filter {
                        it.vendorId == vendorId || repository.getProduct(it.productId)?.vendorId == vendorId
                    }
                    val vendorSubtotal = vendorLines.sumOf { it.unitPrice * it.quantity }

                    ElevatedCard(
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            // Header: Order ID & Date
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

                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(10.dp))

                            // Items in this order belonging to vendor
                            Text(
                                "Store Items (${vendorLines.sumOf { it.quantity }} units):",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            vendorLines.forEach { line ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "${line.quantity}x ${line.productName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        formatPrice(line.unitPrice * line.quantity),
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        SuccessGreen.copy(alpha = 0.08f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Vendor Order Payout:", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Text(formatPrice(vendorSubtotal), fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }

                            Spacer(Modifier.height(10.dp))

                            // Shipping address & payment
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Place, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(order.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Payments, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Paid via: ${order.paymentMethod}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Spacer(Modifier.height(14.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(12.dp))

                            // Status Progression Actions
                            Text("Update Fulfillment Status:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                when (order.status) {
                                    "Confirmed" -> {
                                        Button(
                                            onClick = {
                                                scope.launch { repository.updateOrderStatus(order.id, "Preparing") }
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Filled.Restaurant, null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Pack / Prepare")
                                        }
                                    }
                                    "Preparing" -> {
                                        Button(
                                            onClick = {
                                                scope.launch { repository.updateOrderStatus(order.id, "Shipped") }
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Filled.LocalShipping, null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Dispatch / Ship")
                                        }
                                    }
                                    "Shipped" -> {
                                        Button(
                                            onClick = {
                                                scope.launch { repository.updateOrderStatus(order.id, "Delivered") }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Mark Delivered")
                                        }
                                    }
                                    "Delivered" -> {
                                        Surface(
                                            color = SuccessGreen.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Filled.Check, null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
                                                Spacer(Modifier.width(8.dp))
                                                Text("Order successfully delivered to customer!", fontWeight = FontWeight.Bold, color = SuccessGreen, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                    else -> {
                                        Text("Status: ${order.status}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }

                                if (order.status != "Delivered" && order.status != "Cancelled") {
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch { repository.updateOrderStatus(order.id, "Cancelled") }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Filled.Close, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
