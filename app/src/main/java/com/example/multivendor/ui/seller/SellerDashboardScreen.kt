package com.example.multivendor.ui.seller

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.Order
import com.example.multivendor.data.Product
import com.example.multivendor.data.Vendor
import com.example.multivendor.theme.BrandOrange
import com.example.multivendor.theme.BrandTeal
import com.example.multivendor.theme.StarAmber
import com.example.multivendor.theme.SuccessGreen
import com.example.multivendor.ui.components.EmptyState
import com.example.multivendor.ui.components.ProductThumbnail
import com.example.multivendor.ui.components.VerifiedBadge
import com.example.multivendor.ui.components.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    initialVendorId: String? = null,
    onBack: () -> Unit,
    onAddProduct: (String) -> Unit,
    onEditProduct: (String, String) -> Unit,
    onViewOrders: (String) -> Unit,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SellerDashboardViewModel = viewModel {
        SellerDashboardViewModel(initialVendorId, AppContainer.repository)
    }
) {
    val vendors by viewModel.vendors.collectAsState()
    val activeVendor by viewModel.selectedVendor.collectAsState()
    val products by viewModel.displayedProducts.collectAsState()
    val allVendorProducts by viewModel.vendorProducts.collectAsState()
    val orders by viewModel.vendorOrders.collectAsState()
    val earnings by viewModel.vendorEarnings.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentFilter by viewModel.stockFilter.collectAsState()

    var showStorePicker by remember { mutableStateOf(false) }
    var showCreateStoreDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    val currentVendorId = activeVendor?.id.orEmpty()
    val lowStockCount = allVendorProducts.count { it.stock in 1..10 }
    val outOfStockCount = allVendorProducts.count { it.stock == 0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Seller Portal 🏪", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showStorePicker = true }
                        ) {
                            Text(
                                text = activeVendor?.name ?: "Select Store",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Switch store",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateStoreDialog = true }) {
                        Icon(Icons.Filled.Store, contentDescription = "New Store", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentVendorId.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { onAddProduct(currentVendorId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                        Spacer(Modifier.width(8.dp))
                        Text("Add Product", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Store Header Card
            item {
                activeVendor?.let { vendor ->
                    StoreSummaryCard(
                        vendor = vendor,
                        onSwitchStore = { showStorePicker = true },
                        onNewStore = { showCreateStoreDialog = true }
                    )
                }
            }

            // Quick Analytics Metrics
            item {
                Text(
                    "Store Performance 📊",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Revenue",
                        value = formatPrice(earnings),
                        subtitle = "from orders",
                        icon = Icons.Filled.Payments,
                        iconTint = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Orders",
                        value = orders.size.toString(),
                        subtitle = "${orders.count { it.status == "Confirmed" }} pending",
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        iconTint = BrandOrange,
                        modifier = Modifier.weight(1f),
                        onClick = { onViewOrders(currentVendorId) }
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Catalog",
                        value = allVendorProducts.size.toString(),
                        subtitle = "active items",
                        icon = Icons.Filled.Inventory,
                        iconTint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Stock Alert",
                        value = (lowStockCount + outOfStockCount).toString(),
                        subtitle = if (outOfStockCount > 0) "$outOfStockCount out of stock" else "$lowStockCount low stock",
                        icon = Icons.Filled.Warning,
                        iconTint = if (outOfStockCount > 0) MaterialTheme.colorScheme.error else StarAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Actions Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onAddProduct(currentVendorId) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("New Product")
                    }
                    OutlinedButton(
                        onClick = { onViewOrders(currentVendorId) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Fulfill (${orders.size})")
                    }
                }
            }

            // Inventory Section Header & Search
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Product Inventory 📦",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${allVendorProducts.size} total items",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search your store items...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(StockFilter.entries) { filter ->
                            FilterChip(
                                selected = currentFilter == filter,
                                onClick = { viewModel.stockFilter.value = filter },
                                label = {
                                    val count = when (filter) {
                                        StockFilter.ALL -> allVendorProducts.size
                                        StockFilter.LOW_STOCK -> lowStockCount
                                        StockFilter.OUT_OF_STOCK -> outOfStockCount
                                    }
                                    Text("${filter.label} ($count)")
                                },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Products List
            if (products.isEmpty()) {
                item {
                    EmptyState(
                        title = "No products found",
                        subtitle = if (allVendorProducts.isEmpty()) "Tap 'Add Product' to list your first item." else "Try adjusting your search or stock filter.",
                        icon = Icons.Filled.Inventory
                    )
                }
            } else {
                items(products, key = { it.id }) { product ->
                    SellerProductRow(
                        product = product,
                        onClick = { onProductClick(product.id) },
                        onEdit = { onEditProduct(currentVendorId, product.id) },
                        onDelete = { productToDelete = product },
                        onIncreaseStock = { viewModel.quickAdjustStock(product.id, +5) },
                        onDecreaseStock = { viewModel.quickAdjustStock(product.id, -1) }
                    )
                }
            }

            // Recent Orders Preview
            if (orders.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recent Store Orders 🛒",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { onViewOrders(currentVendorId) }) {
                            Text("View All (${orders.size})")
                        }
                    }
                }
                items(orders.take(3), key = { it.id }) { order ->
                    SellerRecentOrderCard(
                        order = order,
                        vendorId = currentVendorId,
                        onUpdateStatus = { newStatus -> viewModel.updateOrderStatus(order.id, newStatus) },
                        onViewDetails = { onViewOrders(currentVendorId) }
                    )
                }
            }

            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }

    // Store Switcher Dropdown Dialog / Menu
    if (showStorePicker) {
        AlertDialog(
            onDismissRequest = { showStorePicker = false },
            title = { Text("Select Store to Manage 🏬", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    vendors.forEach { v ->
                        val isSelected = v.id == currentVendorId
                        ElevatedCard(
                            onClick = {
                                viewModel.selectVendor(v.id)
                                showStorePicker = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(v.name, fontWeight = FontWeight.Bold)
                                    Text("${v.location} • ⭐ ${v.rating}", style = MaterialTheme.typography.bodySmall)
                                }
                                if (isSelected) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "Active",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = {
                            showStorePicker = false
                            showCreateStoreDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Add, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Register New Store")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStorePicker = false }) { Text("Close") }
            }
        )
    }

    // Delete Confirmation Dialog
    productToDelete?.let { product ->
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product?") },
            text = { Text("Are you sure you want to remove '${product.name}' from your store catalog? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(product.id)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // Register New Store Dialog
    if (showCreateStoreDialog) {
        CreateStoreDialog(
            onDismiss = { showCreateStoreDialog = false },
            onCreate = { name, desc, loc ->
                viewModel.registerNewStore(name, desc, loc)
                showCreateStoreDialog = false
            }
        )
    }
}

@Composable
private fun StoreSummaryCard(
    vendor: Vendor,
    onSwitchStore: () -> Unit,
    onNewStore: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏪", style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                vendor.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (vendor.isVerified) {
                                Spacer(Modifier.width(6.dp))
                                VerifiedBadge()
                            }
                        }
                        Text(
                            vendor.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                FilledTonalButton(
                    onClick = onSwitchStore,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Switch")
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                vendor.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "⭐ ${vendor.rating}",
                    fontWeight = FontWeight.Bold,
                    color = StarAmber,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "• Joined ${vendor.joinedYear}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "• ${vendor.responseTime} response",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = iconTint, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SellerProductRow(
    product: Product,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onIncreaseStock: () -> Unit,
    onDecreaseStock: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Real photo thumbnail with gradient/emoji fallback
                ProductThumbnail(
                    product = product,
                    modifier = Modifier.size(56.dp),
                    cornerRadius = 12,
                    emojiFontSize = 24
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            formatPrice(product.price),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (product.originalPrice != null && product.originalPrice > product.price) {
                            Spacer(Modifier.width(6.dp))
                            Text(
                                formatPrice(product.originalPrice),
                                style = MaterialTheme.typography.bodySmall,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                // Edit & Delete icons
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(10.dp))

            // Stock & Quick Adjust Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (statusColor, statusLabel) = when {
                        product.stock == 0 -> MaterialTheme.colorScheme.error to "Out of Stock"
                        product.stock <= 10 -> StarAmber to "Low Stock: ${product.stock}"
                        else -> SuccessGreen to "${product.stock} in Stock"
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        statusLabel,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }

                // Quick + / - Stock buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Stock:", style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.width(6.dp))
                    FilledTonalButton(
                        onClick = onDecreaseStock,
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "-1", modifier = Modifier.size(14.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${product.stock}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.width(6.dp))
                    FilledTonalButton(
                        onClick = onIncreaseStock,
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "+5", modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerRecentOrderCard(
    order: Order,
    vendorId: String,
    onUpdateStatus: (String) -> Unit,
    onViewDetails: () -> Unit
) {
    var expandedMenu by remember { mutableStateOf(false) }
    val vendorLines = order.lines.filter { it.vendorId == vendorId }
    val vendorSubtotal = vendorLines.sumOf { it.unitPrice * it.quantity }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewDetails),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text(order.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box {
                    Surface(
                        color = when (order.status) {
                            "Confirmed" -> MaterialTheme.colorScheme.primaryContainer
                            "Preparing" -> StarAmber.copy(alpha = 0.2f)
                            "Shipped" -> BrandOrange.copy(alpha = 0.2f)
                            "Delivered" -> SuccessGreen.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { expandedMenu = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                order.status,
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
                            Icon(Icons.Filled.KeyboardArrowDown, null, modifier = Modifier.size(16.dp))
                        }
                    }
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        listOf("Confirmed", "Preparing", "Shipped", "Delivered", "Cancelled").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    onUpdateStatus(status)
                                    expandedMenu = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            vendorLines.forEach { line ->
                Text(
                    "• ${line.quantity}x ${line.productName} (${formatPrice(line.unitPrice)})",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Store Payout:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                Text(formatPrice(vendorSubtotal), fontWeight = FontWeight.Bold, color = SuccessGreen)
            }
        }
    }
}

@Composable
private fun CreateStoreDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Open a New Store 🏬", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
                    label = { Text("Store Name *") },
                    placeholder = { Text("e.g. Apex Electronics") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it; error = null },
                    label = { Text("Location / City *") },
                    placeholder = { Text("e.g. Pune, India") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; error = null },
                    label = { Text("Store Description *") },
                    placeholder = { Text("What makes your store unique?") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || location.isBlank() || description.isBlank()) {
                        error = "Please fill in all fields"
                    } else {
                        onCreate(name, description, location)
                    }
                }
            ) {
                Text("Create Store")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
