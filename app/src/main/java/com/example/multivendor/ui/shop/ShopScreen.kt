package com.example.multivendor.ui.shop

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.Product
import com.example.multivendor.theme.StarAmber
import com.example.multivendor.ui.components.AvenueTopBar
import com.example.multivendor.ui.components.NotificationCenterDialog
import com.example.multivendor.ui.components.SelectLocationDialog
import com.example.multivendor.ui.components.formatPrice

@Composable
fun ShopScreen(
    onProductClick: (String) -> Unit,
    onVendorClick: (String) -> Unit,
    onCategoryCatalogClick: ((String?) -> Unit)? = null,
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ShopViewModel = viewModel { ShopViewModel(AppContainer.repository) }
) {
    val uiState by viewModel.uiState.collectAsState()
    val wishlist by viewModel.wishlistIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All ✨") }
    var showLocationPicker by remember { mutableStateOf(false) }
    var deliveryLocation by remember { mutableStateOf("Indiranagar, 12th Main, Bengaluru") }
    var showVoiceSearch by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

    val categoryChips = listOf(
        "All ✨",
        "Artisanal Studio",
        "Audio & Tech",
        "Home & Living"
    )

    val bestsellers = remember(uiState.products, selectedCategoryFilter, searchQuery) {
        var list = uiState.products
        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
        } else {
            when (selectedCategoryFilter) {
                "Artisanal Studio" -> list = list.filter { it.categoryId == "home" || it.tags.contains("artisan") }
                "Audio & Tech" -> list = list.filter { it.categoryId == "electronics" }
                "Home & Living" -> list = list.filter { it.categoryId == "home" }
                else -> {
                    // Give priority to the iconic hero pair from Image 5: Aura Wireless Studio & Amber Vanilla Candle
                    val aura = list.find { it.id == "p_aura" } ?: list.find { it.id == "p1" }
                    val candle = list.find { it.id == "p_candle_amber" }
                    val others = list.filter { it.id != aura?.id && it.id != candle?.id }
                    list = listOfNotNull(aura, candle) + others
                }
            }
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // 1. Top Bar: Avenue & Co.
        AvenueTopBar(
            onProfileClick = onProfileClick,
            onNotificationClick = { showNotifications = true }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 2. Deliver To Header: Deliver to Priya ⌵, Indiranagar, 20M EXPRESS
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showLocationPicker = true }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NearMe,
                            contentDescription = "Location",
                            tint = Color(0xFF0F2557),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Deliver to Priya",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F2557)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF0F2557),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = deliveryLocation,
                                fontSize = 11.5.sp,
                                color = Color(0xFF64748B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // 20M EXPRESS Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "⚡ 20M EXPRESS",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }
                }
            }


            // 4. Search Bar with Mic and QR Scanner icons
            item(span = { GridItemSpan(maxLineSpan) }) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search for products",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(19.dp)
                        )
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            IconButton(onClick = { showVoiceSearch = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Mic,
                                    contentDescription = "Voice Search",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                            IconButton(onClick = { showBarcodeScanner = true }) {
                                Icon(
                                    imageVector = Icons.Filled.CropFree,
                                    contentDescription = "Scan QR",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF0F2557),
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    )
                )
            }

            // 5. Hero Spotlight Banner (Dark Royal Blue Gradient)
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0F2557),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0A192F), Color(0xFF0F2557), Color(0xFF1E3A8A))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "✨ CURATED SPOTLIGHT",
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.8.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = "Drops Today",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Text(
                                text = "Festive Craft Edition — Up to 50% OFF Handcrafted Treasures",
                                fontSize = 17.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 22.sp
                            )

                            Text(
                                text = "Direct from 14 verified indie studios across Bengaluru. Hand-checked for purity & design excellence.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                lineHeight = 16.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🎟️ ", fontSize = 11.sp)
                                        Text(
                                            text = "CRAFT10",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onCategoryCatalogClick?.invoke(null) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF0F2557)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Explore →",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Trust Badges Row (Horizontal Scroll)
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    item {
                        TrustBadgeItem(
                            icon = "🛍️",
                            title = "Single Cart",
                            subtitle = "One payment, one box"
                        )
                    }
                    item {
                        TrustBadgeItem(
                            icon = "🛡️",
                            title = "100% Indie",
                            subtitle = "Strict vendor audit"
                        )
                    }
                    item {
                        TrustBadgeItem(
                            icon = "🔒",
                            title = "Escrow Escort",
                            subtitle = "Protected payout"
                        )
                    }
                }
            }

            // 7. Category Filter Chips (All ✨, Artisanal Studio, Audio & Tech, etc.)
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(categoryChips) { chip ->
                        val isSelected = selectedCategoryFilter == chip
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color(0xFF0F2557) else Color.White,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF0F2557) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.clickable {
                                selectedCategoryFilter = chip
                                if (chip != "All ✨") {
                                    // Also allow quick drilldown into category catalog
                                    onCategoryCatalogClick?.invoke(
                                        when (chip) {
                                            "Audio & Tech" -> "electronics"
                                            "Artisanal Studio" -> "home"
                                            "Home & Living" -> "home"
                                            else -> null
                                        }
                                    )
                                }
                            }
                        ) {
                            Text(
                                text = chip,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF334155),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // 8. Section Header: "Bestsellers Near You", "• BENGALURU HUB • 2-HR DISPATCH"
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Bestsellers Near You",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F2557)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2563EB))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "BENGALURU HUB • 2-HR DISPATCH",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFEFF6FF),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onCategoryCatalogClick?.invoke(null) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = "Filter",
                                tint = Color(0xFF1E3A8A),
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }

            // 9. 2-Column Product Cards
            items(bestsellers, key = { it.id }) { product ->
                val displayVendorName = when (product.id) {
                    "p_aura", "p1" -> "TechPulse Hub"
                    "p_candle_amber" -> "Botanica Candle Co"
                    else -> viewModel.vendorName(product.vendorId)
                }

                HomeProductCard(
                    product = product,
                    vendorName = displayVendorName,
                    isWishlisted = wishlist.contains(product.id),
                    onClick = { onProductClick(product.id) },
                    onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                    onAddToCart = { viewModel.addToCart(product.id) }
                )
            }

            // 10. Bottom Craft Notice Card
            item(span = { GridItemSpan(maxLineSpan) }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFDBEAFE),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Storefront,
                                    contentDescription = null,
                                    tint = Color(0xFF1E3A8A),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Every purchase directly supports independent Bengaluru craft studios & neighborhood artisans.",
                            fontSize = 12.sp,
                            color = Color(0xFF334155),
                            lineHeight = 17.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    if (showLocationPicker) {
        SelectLocationDialog(
            currentLocation = deliveryLocation,
            onDismiss = { showLocationPicker = false },
            onSelect = {
                deliveryLocation = it
                showLocationPicker = false
            }
        )
    }

    if (showVoiceSearch) {
        VoiceSearchDialog(
            onDismiss = { showVoiceSearch = false },
            onQueryRecognized = { recognized ->
                searchQuery = recognized
            }
        )
    }

    if (showBarcodeScanner) {
        BarcodeScannerDialog(
            onDismiss = { showBarcodeScanner = false },
            onProductDetected = { scannedId ->
                onProductClick(scannedId)
            }
        )
    }

    if (showNotifications) {
        NotificationCenterDialog(
            onDismiss = { showNotifications = false },
            onNavigateToOrders = onProfileClick
        )
    }
}

@Composable
fun TrustBadgeItem(
    icon: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFEFF6FF),
        border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = icon, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2557)
                )
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
fun HomeProductCard(
    product: Product,
    vendorName: String,
    isWishlisted: Boolean,
    onClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFFF1F5F9))
            ) {
                if (!product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Discount Pill
                val discount = product.discountPercent ?: 35
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F2557),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "-$discount% OFF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                // Wishlist Heart Icon
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clickable { onWishlistToggle() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Color(0xFFE11D48) else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = vendorName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(12.dp)
                    )
                }

                Text(
                    text = product.name,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2557),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Rating (e.g. ⭐ 4.8 (124))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = StarAmber,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${String.format(java.util.Locale.US, "%.1f", product.rating)} (${product.reviews})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F2557)
                    )
                }

                // Price Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = formatPrice(product.price),
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F2557)
                    )
                    if (product.originalPrice != null && product.originalPrice > product.price) {
                        Text(
                            text = formatPrice(product.originalPrice),
                            fontSize = 11.5.sp,
                            color = Color(0xFF94A3B8),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
            }
        }
    }
}
