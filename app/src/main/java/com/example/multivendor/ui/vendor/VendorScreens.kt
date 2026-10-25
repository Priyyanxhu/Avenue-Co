package com.example.multivendor.ui.vendor

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.MarketplaceRepository
import com.example.multivendor.data.Product
import com.example.multivendor.data.Vendor
import com.example.multivendor.theme.StarAmber
import com.example.multivendor.ui.components.AvenueTopBar
import com.example.multivendor.ui.components.ProductCard
import com.example.multivendor.ui.components.VerifiedBadge
import com.example.multivendor.ui.components.formatPrice
import kotlinx.coroutines.launch

class VendorListViewModel(
    private val repository: MarketplaceRepository = AppContainer.repository
) : ViewModel() {
    val vendors = repository.vendors
    val products = repository.products

    fun productsFor(vendorId: String): List<Product> =
        repository.productsByVendor(vendorId)

    fun countFor(vendorId: String) = repository.productsByVendor(vendorId).size
    fun addToCart(id: String) { viewModelScope.launch { repository.addToCart(id) } }
    fun toggleWishlist(id: String) { viewModelScope.launch { repository.toggleWishlist(id) } }
}

@Composable
fun VendorListScreen(
    onVendorClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    onApplyVendorClick: () -> Unit = {},
    viewModel: VendorListViewModel = viewModel { VendorListViewModel(AppContainer.repository) }
) {
    val vendors by viewModel.vendors.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Stores (6)") }

    val displayedVendors = remember(vendors, searchQuery, selectedFilter) {
        var list = vendors
        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true)
            }
        }
        when (selectedFilter) {
            "Top Rated 4.8+" -> list.filter { it.rating >= 4.8 }
            else -> list
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // 1. Top Bar: Avenue & Co.
        AvenueTopBar(
            onProfileClick = onProfileClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Location Row: Delivering to INDIRANAGAR, 560038
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Delivering to INDIRANAGAR, 560038",
                        fontSize = 12.sp,
                        color = Color(0xFF475569),
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.clickable { }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Change",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF334155),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // 3. Screen Title & Subtitle
            item {
                Column {
                    Text(
                        text = "Verified Makers &\nStores",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F2557),
                        letterSpacing = (-0.5).sp,
                        lineHeight = 34.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${displayedVendors.size} verified independent vendors delivering in Indiranagar",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // 4. Search Box
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search store name, atelier, or craft specialty",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
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

            // 5. Filter Chips Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        "All Stores (6)" to true,
                        "Top Rated 4.8+" to false,
                        "Same-Day Dispatch" to false
                    )
                    items(filters) { (filter, _) ->
                        val isSelected = selectedFilter.startsWith(filter.take(5))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color(0xFF0F2557) else Color.White,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFF0F2557) else Color(0xFFE2E8F0)
                            ),
                            modifier = Modifier.clickable {
                                selectedFilter = if (isSelected && filter != "All Stores (6)") "All Stores (6)" else filter
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                } else if (filter.contains("Top Rated")) {
                                    Text("⭐ ", fontSize = 11.sp)
                                } else if (filter.contains("Same-Day")) {
                                    Text("⚡ ", fontSize = 11.sp)
                                }
                                Text(
                                    text = filter,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            }

            // 6. Guarantee Banner: 100% Curated & Audited • Strict QC
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F2557),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "100% Curated & Audited • Strict QC",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Avenue Guarantee: Direct workshop dispatch with verified provenance, ethical craft audit, and tamper-evident packaging.",
                                fontSize = 11.5.sp,
                                color = Color(0xFF94A3B8),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // 7. Store Cards (TechHub, StyleNest, HomeEssentials, BeautyBloom, Sportify)
            items(displayedVendors, key = { it.id }) { vendor ->
                CuratedVendorCard(
                    vendor = vendor,
                    products = viewModel.productsFor(vendor.id),
                    onClick = { onVendorClick(vendor.id) }
                )
            }

            // 8. Bottom Maker Collective Callout
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0F2557),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Storefront,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "VENDOR COLLECTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Are you an independent maker?",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Join our verified merchant collective and deliver with Avenue fulfillment networks. Direct customer access, zero setup fees, and curated showcase placement.",
                                fontSize = 12.5.sp,
                                color = Color(0xFF94A3B8),
                                lineHeight = 18.sp
                            )
                        }

                        Button(
                            onClick = onApplyVendorClick,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF0F2557)
                            ),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                text = "Apply as a Vendor →",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CuratedVendorCard(
    vendor: Vendor,
    products: List<Product>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initials = remember(vendor.name) {
        vendor.name.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
            .ifEmpty { "ST" }
    }

    val (badgeText, tags, highlightText) = remember(vendor.id) {
        when (vendor.id) {
            "techhub" -> Triple("Express", listOf("⏱ 1hr response time", "📦 Express Available"), null)
            "stylenest" -> Triple("Artisan", listOf("🌱 Sustainable packaging", "♻ Zero Plastic"), null)
            "homeess" -> Triple("Crafted", listOf("🏺 Artisan Crafted", "📦 Fragile Handled"), null)
            "beautybloom" -> Triple(
                "Organic",
                listOf("🧴 Handcrafted Batches", "🐰 Cruelty-Free"),
                "Small Batch Botanical Apothecary\nCold-pressed cold-filtered oils & balms"
            )
            "sportify" -> Triple("Gear", listOf("⚡ Fast Dispatch", "↩ 7-Day Easy Returns"), null)
            else -> Triple("Verified", listOf("⏱ ${vendor.responseTime}", "📦 Express Available"), null)
        }
    }

    val reviewsCount = remember(vendor.id) {
        when (vendor.id) {
            "techhub" -> "1,120"
            "stylenest" -> "840"
            "homeess" -> "620"
            "beautybloom" -> "410"
            "sportify" -> "290"
            else -> "500+"
        }
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Initials circle, Store name, verified check, pill, location & rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Initials Avatar
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = initials,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = vendor.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F2557),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${vendor.location.split(",").first()} • ${vendor.rating}★ ($reviewsCount reviews)",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B)
                    )
                }

                // Category Tag Pill (Express, Artisan, Crafted, Organic, Gear)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Tags row (e.g. 1hr response time, Express Available)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Preview Section: Either 2 Product Previews OR Highlight banner
            if (highlightText != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌿 ", fontSize = 18.sp)
                        Column {
                            val lines = highlightText.split("\n")
                            Text(
                                text = lines[0],
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                            if (lines.size > 1) {
                                Text(
                                    text = lines[1],
                                    fontSize = 11.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }
                }
            } else if (products.isNotEmpty()) {
                val previewProducts = products.take(2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    previewProducts.forEach { product ->
                        VendorProductPreviewItem(
                            product = product,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Button: Browse full store catalog →
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() }
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val buttonLabel = if (vendor.id == "beautybloom") {
                        "Browse 9 apothecary items →"
                    } else {
                        "Browse full store catalog →"
                    }
                    Text(
                        text = buttonLabel,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E3A8A)
                    )
                }
            }
        }
    }
}

@Composable
fun VendorProductPreviewItem(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(Color(0xFFE2E8F0))
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
            }

            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F2557),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatPrice(product.price),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2557)
                )
            }
        }
    }
}

class VendorDetailViewModel(
    private val vendorId: String,
    private val repository: MarketplaceRepository = AppContainer.repository
) : ViewModel() {
    val wishlist = repository.wishlistIds
    fun vendor() = repository.getVendor(vendorId)
    fun products() = repository.productsByVendor(vendorId)
    fun vendorName(id: String) = repository.getVendor(id)?.name ?: ""
    fun addToCart(id: String) { viewModelScope.launch { repository.addToCart(id) } }
    fun toggleWishlist(id: String) { viewModelScope.launch { repository.toggleWishlist(id) } }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDetailScreen(
    vendorId: String,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onManageStore: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: VendorDetailViewModel = viewModel(key = "vd-$vendorId") { VendorDetailViewModel(vendorId, AppContainer.repository) }
) {
    val wishlist by viewModel.wishlist.collectAsState()
    val vendor = remember(vendorId) { viewModel.vendor() }
    if (vendor == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Seller not found") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                )
            }
        ) { padding ->
            Text("Seller not found", Modifier.padding(padding).padding(24.dp))
        }
        return
    }
    val products = remember(vendorId) { viewModel.products() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vendor.name, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (onManageStore != null) {
                        androidx.compose.material3.TextButton(onClick = { onManageStore(vendor.id) }) {
                            Text("Manage Store 🏪", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏪", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(vendor.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(vendor.location, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        if (vendor.isVerified) VerifiedBadge()
                        Spacer(Modifier.height(8.dp))
                        Text(vendor.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "⭐ ${vendor.rating} • ${vendor.totalSales} sales • Since ${vendor.joinedYear} • ${vendor.responseTime} response",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("${products.size} products from this seller", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            if (products.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    com.example.multivendor.ui.components.EmptyState(
                        title = "No products yet",
                        subtitle = "This seller hasn't listed products.",
                        icon = Icons.Filled.Store
                    )
                }
            } else {
                items(products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        vendorName = viewModel.vendorName(product.vendorId),
                        isWishlisted = wishlist.contains(product.id),
                        onClick = { onProductClick(product.id) },
                        onAddToCart = { viewModel.addToCart(product.id) },
                        onWishlistToggle = { viewModel.toggleWishlist(product.id) }
                    )
                }
            }
        }
    }
}
