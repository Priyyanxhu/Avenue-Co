package com.example.multivendor.ui.cart

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.CartLine
import com.example.multivendor.data.MarketplaceRepository
import com.example.multivendor.data.Product
import com.example.multivendor.ui.components.AvenueTopBar
import com.example.multivendor.ui.components.ProductThumbnail
import com.example.multivendor.ui.components.QuantityStepper
import com.example.multivendor.ui.components.formatPrice
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CartUiState(
    val lines: List<CartLine> = emptyList(),
    val subtotal: Double = 0.0,
    val promoCode: String = "",
    val discount: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0
)

class CartViewModel(
    private val repository: MarketplaceRepository = AppContainer.repository
) : ViewModel() {
    private val promo = kotlinx.coroutines.flow.MutableStateFlow("")

    val promoCode = promo as kotlinx.coroutines.flow.StateFlow<String>
    val wishlistIds = repository.wishlistIds
    val trendingProducts = repository.products

    val uiState = combine(repository.cartLines, repository.cartSubtotal, promo) { lines, subtotal, code ->
        val normalized = code.trim().uppercase().takeIf { it.isNotEmpty() }
        val discount = repository.discountForPromo(normalized, subtotal)
        val shipping = repository.shippingFor(subtotal - discount, normalized)
        CartUiState(
            lines = lines,
            subtotal = subtotal,
            promoCode = code,
            discount = discount,
            shipping = shipping,
            total = (subtotal - discount + shipping).coerceAtLeast(0.0)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartUiState())

    fun setPromo(value: String) { promo.value = value }

    fun updateQty(productId: String, qty: Int) {
        viewModelScope.launch { repository.updateCartQuantity(productId, qty) }
    }
    fun remove(productId: String) {
        viewModelScope.launch { repository.removeFromCart(productId) }
    }
    fun clear() {
        viewModelScope.launch { repository.clearCart() }
    }
    fun addToCart(productId: String) {
        viewModelScope.launch { repository.addToCart(productId) }
    }
    fun toggleWishlist(productId: String) {
        viewModelScope.launch { repository.toggleWishlist(productId) }
    }
}

@Composable
fun CartScreen(
    onProductClick: (String) -> Unit,
    onCheckout: () -> Unit,
    onBrowse: () -> Unit,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
    viewModel: CartViewModel = viewModel { CartViewModel(AppContainer.repository) }
) {
    val state by viewModel.uiState.collectAsState()
    val wishlist by viewModel.wishlistIds.collectAsState()
    val allProducts by viewModel.trendingProducts.collectAsState()
    var promoInput by remember { mutableStateOf("") }
    var menuOpen by remember { mutableStateOf(false) }

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
            // Title Bar: Shopping Cart (0 items) + More Options
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val itemCount = state.lines.sumOf { it.quantity }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Shopping Cart",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F2557),
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "($itemCount items)",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Box {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFEFF6FF),
                            modifier = Modifier.size(34.dp)
                        ) {
                            IconButton(onClick = { menuOpen = true }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreHoriz,
                                    contentDescription = "Options",
                                    tint = Color(0xFF1E3A8A),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Clear cart") },
                                onClick = {
                                    viewModel.clear()
                                    menuOpen = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Continue shopping") },
                                onClick = {
                                    menuOpen = false
                                    onBrowse()
                                }
                            )
                        }
                    }
                }
            }

            if (state.lines.isEmpty()) {
                // Image 3: Empty State Artwork & Copy
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Circular smiling shopping bag illustration with sparkles
                        Box(
                            modifier = Modifier.size(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Soft blue circle ring
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFEFF6FF),
                                modifier = Modifier.size(110.dp)
                            ) {}

                            // Sparkle top-left
                            Text(
                                text = "✦",
                                fontSize = 16.sp,
                                color = Color(0xFF3B82F6),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 14.dp, top = 20.dp)
                            )

                            // Star bottom-left
                            Text(
                                text = "☆",
                                fontSize = 16.sp,
                                color = Color(0xFF3B82F6),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 12.dp, bottom = 12.dp)
                            )

                            // Sparkle top-right
                            Text(
                                text = "✦",
                                fontSize = 14.sp,
                                color = Color(0xFF60A5FA),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(end = 18.dp, top = 16.dp)
                            )

                            // Center cute smiling bag
                            Surface(
                                shape = CircleShape,
                                color = Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("🛍️", fontSize = 32.sp)
                                        Text("‿", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2557))
                                    }
                                }
                            }
                        }

                        // Headline & Subtitle
                        Text(
                            text = "Your cart is feeling light",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F2557),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Discover unique handcrafted creations and tech gear from multiple independent makers — all delivered in one seamless order.",
                            fontSize = 12.5.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            lineHeight = 18.sp
                        )

                        // Start Exploring Stores Button
                        Button(
                            onClick = onBrowse,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0F2557),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text(
                                text = "Start Exploring Stores →",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // "Why Shop Avenue & Co." Section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = Color(0xFF0F2557),
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Why Shop Avenue & Co.",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F2557)
                            )
                        }

                        // Card 1: Single Box Delivery
                        BenefitCard(
                            icon = "📦",
                            title = "Single Box Delivery",
                            description = "Shop from 10 different verified sellers, receive everything together in one consolidated shipment."
                        )

                        // Card 2: Buyer Protection
                        BenefitCard(
                            icon = "🛡️",
                            title = "Buyer Protection",
                            description = "Full escrow guarantee holds your payment safely until you inspect and approve your received items."
                        )

                        // Card 3: Direct Maker Impact
                        BenefitCard(
                            icon = "🤝",
                            title = "Direct Maker Impact",
                            description = "85% of each purchase goes directly back to the independent artisan, workshop, and boutique founder."
                        )
                    }
                }

                // "Trending Picks for You" Section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Trending Picks for You",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F2557)
                                )
                                Text(
                                    text = "Handpicked daily from verified makers",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Text(
                                text = "See all →",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2563EB),
                                modifier = Modifier.clickable { onBrowse() }
                            )
                        }

                        // 2 Cards: Pure Botanica Candle & Nordic Roast Coffee
                        val trendingItems = remember(allProducts) {
                            val candle = allProducts.find { it.id == "p_candle_botanica" }
                            val coffee = allProducts.find { it.id == "p_coffee" }
                            listOfNotNull(candle, coffee)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            trendingItems.forEach { product ->
                                TrendingProductMiniCard(
                                    product = product,
                                    vendorName = if (product.id == "p_candle_botanica") "Terrene Studio" else "Fjord Co-op",
                                    isWishlisted = wishlist.contains(product.id),
                                    onProductClick = { onProductClick(product.id) },
                                    onWishlistToggle = { viewModel.toggleWishlist(product.id) },
                                    onQuickAdd = { viewModel.addToCart(product.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            } else {
                // Regular cart items with quantity steppers and checkout
                items(state.lines, key = { it.product.id }) { line ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProductThumbnail(
                                product = line.product,
                                modifier = Modifier.size(76.dp)
                            )
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = line.product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F2557),
                                    maxLines = 2,
                                    modifier = Modifier.clickable { onProductClick(line.product.id) }
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = formatPrice(line.product.price),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF0F2557)
                                    )
                                    if (line.product.originalPrice != null) {
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = formatPrice(line.product.originalPrice),
                                            fontSize = 12.sp,
                                            color = Color(0xFF94A3B8),
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    QuantityStepper(
                                        quantity = line.quantity,
                                        onDecrease = { viewModel.updateQty(line.product.id, line.quantity - 1) },
                                        onIncrease = { viewModel.updateQty(line.product.id, line.quantity + 1) },
                                        max = line.product.stock
                                    )
                                    IconButton(
                                        onClick = { viewModel.remove(line.product.id) },
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(Color(0xFFFEE2E2), CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "Remove",
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Promo code card
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocalOffer, null, tint = Color(0xFF0F2557), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Have a promo? Try CRAFT10 / SAVE10", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = promoInput,
                                    onValueChange = { promoInput = it },
                                    placeholder = { Text("Enter promo code", fontSize = 13.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = { viewModel.setPromo(promoInput) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2557))
                                ) {
                                    Text("Apply", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Consolidated Shipment note & Order Summary
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal", color = Color(0xFF475569), fontSize = 13.sp)
                                Text(formatPrice(state.subtotal), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                            if (state.discount > 0.0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Discount (${state.promoCode})", color = Color(0xFF16A34A), fontSize = 13.sp)
                                    Text("-${formatPrice(state.discount)}", color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Consolidated Delivery", color = Color(0xFF475569), fontSize = 13.sp)
                                Text(
                                    if (state.shipping == 0.0) "FREE" else formatPrice(state.shipping),
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (state.shipping == 0.0) Color(0xFF16A34A) else Color(0xFF0F2557),
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color(0xFF0F2557))
                                Text(formatPrice(state.total), fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = Color(0xFF0F2557))
                            }
                            Spacer(Modifier.height(6.dp))
                            Button(
                                onClick = onCheckout,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2557))
                            ) {
                                Text("Proceed to Checkout →", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BenefitCard(
    icon: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFEFF6FF),
        border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = icon, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2557)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.5.sp,
                    color = Color(0xFF475569),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun TrendingProductMiniCard(
    product: Product,
    vendorName: String,
    isWishlisted: Boolean,
    onProductClick: () -> Unit,
    onWishlistToggle: () -> Unit,
    onQuickAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier.clickable { onProductClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
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

                // Discount badge
                val discount = product.discountPercent
                if (discount != null && discount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDC2626),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "-$discount%",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Wishlist heart
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(30.dp)
                        .clickable { onWishlistToggle() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Color(0xFFE11D48) else Color(0xFF64748B),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(10.dp),
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
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F2557),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = formatPrice(product.price),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F2557)
                    )
                    if (product.originalPrice != null && product.originalPrice > product.price) {
                        Text(
                            text = formatPrice(product.originalPrice),
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onQuickAdd() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🛒 ", fontSize = 11.sp)
                        Text(
                            text = "Quick Add",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    isGreen: Boolean = false,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (isGreen) Color(0xFF16A34A) else Color(0xFF475569),
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isBold) 15.sp else 13.5.sp
        )
        Text(
            text = value,
            color = if (isGreen) Color(0xFF16A34A) else Color(0xFF0F2557),
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = if (isBold) 15.sp else 13.5.sp
        )
    }
}
