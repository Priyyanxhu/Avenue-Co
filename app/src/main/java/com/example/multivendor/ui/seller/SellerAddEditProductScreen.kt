package com.example.multivendor.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.MarketplaceRepository
import com.example.multivendor.data.Product
import com.example.multivendor.theme.BrandOrange
import com.example.multivendor.theme.gradientFor
import com.example.multivendor.ui.components.ProductCard
import com.example.multivendor.ui.components.formatPrice
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerAddEditProductScreen(
    vendorId: String,
    productId: String? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    repository: MarketplaceRepository = AppContainer.repository
) {
    val existingProduct = remember(productId) {
        productId?.let { repository.getProduct(it) }
    }
    val categories by repository.categories.collectAsState()
    val vendor = remember(vendorId) { repository.getVendor(vendorId) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val isEditing = existingProduct != null

    var name by remember { mutableStateOf(existingProduct?.name.orEmpty()) }
    var description by remember { mutableStateOf(existingProduct?.description.orEmpty()) }
    var priceText by remember { mutableStateOf(existingProduct?.price?.let { "%.0f".format(it) }.orEmpty()) }
    var originalPriceText by remember { mutableStateOf(existingProduct?.originalPrice?.let { "%.0f".format(it) }.orEmpty()) }
    var stockText by remember { mutableStateOf(existingProduct?.stock?.toString() ?: "20") }
    var selectedCategoryId by remember { mutableStateOf(existingProduct?.categoryId ?: categories.firstOrNull()?.id ?: "electronics") }
    var tagsText by remember { mutableStateOf(existingProduct?.tags?.joinToString(", ").orEmpty()) }
    var isFeatured by remember { mutableStateOf(existingProduct?.isFeatured ?: false) }
    var isNew by remember { mutableStateOf(existingProduct?.isNew ?: (existingProduct == null)) }
    var gradientSeed by remember { mutableIntStateOf(existingProduct?.gradientSeed ?: (0..13).random()) }
    var imageUrl by remember { mutableStateOf(existingProduct?.imageUrl.orEmpty()) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Live preview Product object
    val previewPrice = priceText.toDoubleOrNull() ?: 999.0
    val previewOriginal = originalPriceText.toDoubleOrNull()
    val previewStock = stockText.toIntOrNull() ?: 10
    val previewProduct = Product(
        id = existingProduct?.id ?: "preview",
        name = if (name.isBlank()) "Product Title Preview" else name,
        description = if (description.isBlank()) "Product description preview goes here." else description,
        price = previewPrice,
        originalPrice = previewOriginal,
        rating = existingProduct?.rating ?: 5.0,
        reviews = existingProduct?.reviews ?: 1,
        categoryId = selectedCategoryId,
        vendorId = vendorId,
        stock = previewStock,
        isFeatured = isFeatured,
        isNew = isNew,
        tags = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        gradientSeed = gradientSeed,
        imageUrl = imageUrl.trim().takeIf { it.isNotBlank() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Product ✏️" else "Add New Product ➕",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Customer Preview Header
            Text(
                "Live Customer Preview 👀",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Box(modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.CenterHorizontally)) {
                ProductCard(
                    product = previewProduct,
                    vendorName = vendor?.name ?: "Your Store",
                    isWishlisted = false,
                    onClick = {},
                    onAddToCart = {},
                    onWishlistToggle = {}
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Product Details 📝",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; errorMessage = null },
                label = { Text("Product Title *") },
                placeholder = { Text("e.g. Ultra Wireless Earbuds") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = it }
            ) {
                val currentCategory = categories.firstOrNull { it.id == selectedCategoryId }
                OutlinedTextField(
                    value = "${currentCategory?.emoji ?: ""} ${currentCategory?.name ?: "Select Category"}",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text("${cat.emoji} ${cat.name}") },
                            onClick = {
                                selectedCategoryId = cat.id
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Price & MRP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it; errorMessage = null },
                    label = { Text("Selling Price (₹) *") },
                    placeholder = { Text("1499") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = originalPriceText,
                    onValueChange = { originalPriceText = it; errorMessage = null },
                    label = { Text("MRP / Original (₹)") },
                    placeholder = { Text("2499") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Discount Badge info if valid
            if (previewProduct.discountPercent != null) {
                Surface(
                    color = BrandOrange.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏷️ ", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Customers will see a ${previewProduct.discountPercent}% OFF discount badge!",
                            fontWeight = FontWeight.Bold,
                            color = BrandOrange,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Stock Quantity
            Column {
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it; errorMessage = null },
                    label = { Text("Available Stock Units *") },
                    placeholder = { Text("50") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 25, 50, 100).forEach { qty ->
                        item {
                            AssistChip(
                                onClick = { stockText = qty.toString() },
                                label = { Text("+$qty units") }
                            )
                        }
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it; errorMessage = null },
                label = { Text("Description *") },
                placeholder = { Text("Highlight key features, battery life, material, warranty, etc.") },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Tags
            OutlinedTextField(
                value = tagsText,
                onValueChange = { tagsText = it },
                label = { Text("Search Tags (comma-separated)") },
                placeholder = { Text("e.g. bluetooth, bass, waterproof") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Product photo URL
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Product Photo URL (https://…)") },
                placeholder = { Text("Paste Unsplash / CDN image link") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                supportingText = {
                    Text(
                        if (imageUrl.isBlank()) "Leave empty to use gradient + emoji art. Live preview above updates instantly."
                        else "Live preview above shows this photo. Blank = fallback art."
                    )
                }
            )

            // Theme Gradient Picker
            Text("Card Aesthetic / Gradient Theme 🎨", fontWeight = FontWeight.SemiBold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(14) { seed ->
                    val (c1, c2) = gradientFor(seed)
                    val isSelected = gradientSeed == seed
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(c1, c2)))
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                                shape = CircleShape
                            )
                            .clickable { gradientSeed = seed },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }

            // Toggles: Featured & New
            ElevatedCard(shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Featured Product ⭐", fontWeight = FontWeight.SemiBold)
                            Text("Showcase this item on the marketplace home banners.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = isFeatured, onCheckedChange = { isFeatured = it })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Mark as New Arrival 🚀", fontWeight = FontWeight.SemiBold)
                            Text("Display a green 'NEW' badge to attract more shoppers.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = isNew, onCheckedChange = { isNew = it })
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Save Button
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull()
                    val original = originalPriceText.takeIf { it.isNotBlank() }?.toDoubleOrNull()
                    val stock = stockText.toIntOrNull()

                    when {
                        name.isBlank() -> errorMessage = "Product title is required."
                        price == null || price <= 0.0 -> errorMessage = "Please enter a valid selling price."
                        stock == null || stock < 0 -> errorMessage = "Please enter a valid stock quantity."
                        description.isBlank() -> errorMessage = "Product description is required."
                        else -> {
                            val tags = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            val productToSave = Product(
                                id = existingProduct?.id ?: "p-${UUID.randomUUID().toString().take(6)}",
                                name = name.trim(),
                                description = description.trim(),
                                price = price,
                                originalPrice = original,
                                rating = existingProduct?.rating ?: 5.0,
                                reviews = existingProduct?.reviews ?: 0,
                                categoryId = selectedCategoryId,
                                vendorId = vendorId,
                                stock = stock,
                                isFeatured = isFeatured,
                                isNew = isNew,
                                tags = tags,
                                gradientSeed = gradientSeed,
                                imageUrl = imageUrl.trim().takeIf { it.isNotBlank() }
                            )

                            scope.launch {
                                if (isEditing) {
                                    repository.updateProduct(productToSave)
                                } else {
                                    repository.addProduct(productToSave)
                                }
                                onBack()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isEditing) "Save Changes" else "Publish Product to Store",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
