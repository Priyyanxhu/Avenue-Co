package com.example.multivendor.ui.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.MarketplaceRepository
import com.example.multivendor.data.RatingBreakdown
import com.example.multivendor.data.Review
import com.example.multivendor.theme.gradientFor
import com.example.multivendor.ui.components.BankOffersCard
import com.example.multivendor.ui.components.CustomerReviewCard
import com.example.multivendor.ui.components.DeliveryEstimatorCard
import com.example.multivendor.ui.components.ProductCard
import com.example.multivendor.ui.components.ProductVariantsSelector
import com.example.multivendor.ui.components.QuantityStepper
import com.example.multivendor.ui.components.RatingBreakdownCard
import com.example.multivendor.ui.components.RatingStars
import com.example.multivendor.ui.components.TrustRow
import com.example.multivendor.ui.components.VerifiedBadge
import com.example.multivendor.ui.components.WriteReviewDialog
import com.example.multivendor.ui.components.formatPrice
import com.example.multivendor.ui.components.productEmoji
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ProductDetailViewModel(
    private val productId: String,
    private val repository: MarketplaceRepository = AppContainer.repository
) : ViewModel() {
    val product = repository.products
    val wishlistIds = repository.wishlistIds
    val reviews = repository.reviewsForProduct(productId)
    val ratingBreakdown = repository.ratingBreakdownFor(productId)

    fun getProduct() = repository.getProduct(productId)
    fun getVendor(vendorId: String) = repository.getVendor(vendorId)
    fun related(product: com.example.multivendor.data.Product) =
        repository.productsByVendor(product.vendorId).filter { it.id != product.id }.take(4)
            .ifEmpty { repository.searchProducts("", product.categoryId, com.example.multivendor.data.SortOption.POPULAR).filter { it.id != product.id }.take(4) }

    fun addToCart(qty: Int) {
        viewModelScope.launch { repository.addToCart(productId, qty) }
    }
    fun toggleWishlist() {
        viewModelScope.launch { repository.toggleWishlist(productId) }
    }
    fun submitReview(rating: Int, title: String, comment: String, reviewerName: String) {
        viewModelScope.launch {
            val newReview = Review(
                id = "rev-" + UUID.randomUUID().toString().take(6),
                productId = productId,
                userName = reviewerName,
                rating = rating,
                title = title,
                comment = comment,
                date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                isVerifiedBuyer = true,
                helpfulCount = 0
            )
            repository.addReview(newReview)
        }
    }
    fun markReviewHelpful(reviewId: String) {
        viewModelScope.launch { repository.markReviewHelpful(reviewId) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    onBack: () -> Unit,
    onProductClick: (String) -> Unit,
    onVendorClick: (String) -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = viewModel(key = "pd-$productId") { ProductDetailViewModel(productId, AppContainer.repository) }
) {
    val wishlist by viewModel.wishlistIds.collectAsState()
    val products by viewModel.product.collectAsState()
    val product = remember(productId, products) { viewModel.getProduct() }
    val reviews by viewModel.reviews.collectAsState(initial = emptyList())
    val ratingBreakdown by viewModel.ratingBreakdown.collectAsState(
        initial = RatingBreakdown(product?.rating ?: 4.0, product?.reviews ?: 0)
    )
    var qty by remember { mutableIntStateOf(1) }
    var reviewFilter by remember { mutableStateOf("All") }
    var showReviewDialog by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (product == null) {
        Scaffold(topBar = { TopAppBar(title = { Text("Not found") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Product not found.")
            }
        }
        return
    }

    val vendor = viewModel.getVendor(product.vendorId)
    val related = remember(product) { viewModel.related(product) }
    val isWishlisted = wishlist.contains(product.id)
    val (c1, c2) = gradientFor(product.gradientSeed)

    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(product.name, maxLines = 1, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh, androidx.compose.foundation.shape.CircleShape)
                                .padding(6.dp)
                                .size(20.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, product.name)
                            putExtra(
                                android.content.Intent.EXTRA_TEXT,
                                "Check out ${product.name} on Avenue & Co. for ${formatPrice(product.price)}! Handcrafted multi-vendor quality: https://avenue.store/p/${product.id}"
                            )
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share product via"))
                    }) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { viewModel.toggleWishlist() }) {
                        Icon(
                            if (isWishlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Color(0xFFE11D48) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 12.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.addToCart(qty)
                            scope.launch { snackbar.showSnackbar("Added $qty × ${product.name} to cart") }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 13.dp)
                    ) {
                        Icon(Icons.Filled.ShoppingCart, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            viewModel.addToCart(qty)
                            onCartClick()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 13.dp)
                    ) { Text("Buy Now", fontWeight = FontWeight.ExtraBold) }
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(16.dp)) {
            Box(
                Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(c1, c2))),
                contentAlignment = Alignment.Center
            ) {
                // Emoji fallback behind the photo.
                Text(productEmoji(product), style = MaterialTheme.typography.displayLarge)
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
                } else {
                    // Decorative depth circles when no photo.
                    Box(
                        Modifier.size(170.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = (-50).dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White.copy(alpha = 0.14f))
                    )
                    Box(
                        Modifier.size(110.dp).align(Alignment.BottomStart).offset(x = (-30).dp, y = 30.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.Black.copy(alpha = 0.10f))
                    )
                }
                if (product.discountPercent != null) {
                    androidx.compose.material3.Surface(
                        shape = RoundedCornerShape(12.dp), color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier.align(Alignment.TopStart).padding(14.dp)
                    ) {
                        Text("-${product.discountPercent}% OFF", Modifier.padding(horizontal = 10.dp, vertical = 6.dp), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge)
                    }
                }
                androidx.compose.material3.Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (product.stock <= 5) MaterialTheme.colorScheme.error else Color.Black.copy(alpha = 0.45f),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp)
                ) {
                    Text(
                        if (product.stock <= 5) "Only ${product.stock} left!" else "In stock • ${product.stock} pcs",
                        Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(vendor?.name ?: "Unknown seller", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Text(product.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            RatingStars(product.rating, product.reviews)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(formatPrice(product.price), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                if (product.originalPrice != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(formatPrice(product.originalPrice), style = MaterialTheme.typography.bodyLarge, textDecoration = TextDecoration.LineThrough, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text("Inclusive of all taxes • Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            if (vendor != null) {
                androidx.compose.material3.ElevatedCard(shape = RoundedCornerShape(14.dp), onClick = { onVendorClick(vendor.id) }) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Sold by ${vendor.name}", fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.width(8.dp))
                            if (vendor.isVerified) VerifiedBadge()
                        }
                        Text("⭐ ${vendor.rating} • ${vendor.location} • ${vendor.responseTime} response", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            Text("Description", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(product.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Quantity", fontWeight = FontWeight.SemiBold)
                QuantityStepper(quantity = qty, onDecrease = { if (qty > 1) qty-- }, onIncrease = { if (qty < product.stock) qty++ }, max = product.stock)
            }
            Spacer(Modifier.height(14.dp))

            // Flipkart / Meesho style Product Variants
            ProductVariantsSelector(categoryId = product.categoryId)

            Spacer(Modifier.height(14.dp))

            // Amazon / Flipkart style Delivery & Pincode Checker
            DeliveryEstimatorCard(initialPincode = "560038")

            Spacer(Modifier.height(14.dp))

            // Bank Offers & Promo Cards
            BankOffersCard(
                onApplyCode = { code ->
                    scope.launch { snackbar.showSnackbar("Coupon $code applied! Discount will reflect at checkout.") }
                }
            )

            Spacer(Modifier.height(14.dp))
            TrustRow()

            // Amazon / Flipkart style Customer Ratings & Reviews Section
            Spacer(Modifier.height(16.dp))
            RatingBreakdownCard(
                breakdown = ratingBreakdown,
                onWriteReviewClick = { showReviewDialog = true }
            )

            // Review Filter Chips & List
            if (reviews.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("All", "5★", "4★", "3★", "2★", "1★")) { filter ->
                        FilterChip(
                            selected = reviewFilter == filter,
                            onClick = { reviewFilter = filter },
                            label = { Text(filter) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                val filteredReviews = when (reviewFilter) {
                    "5★" -> reviews.filter { it.rating == 5 }
                    "4★" -> reviews.filter { it.rating == 4 }
                    "3★" -> reviews.filter { it.rating == 3 }
                    "2★" -> reviews.filter { it.rating == 2 }
                    "1★" -> reviews.filter { it.rating == 1 }
                    else -> reviews
                }
                if (filteredReviews.isEmpty()) {
                    Text(
                        "No reviews matching $reviewFilter.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    filteredReviews.forEach { review ->
                        Spacer(Modifier.height(8.dp))
                        CustomerReviewCard(
                            review = review,
                            onHelpfulClick = { viewModel.markReviewHelpful(review.id) }
                        )
                    }
                }
            } else {
                Spacer(Modifier.height(8.dp))
                Text(
                    "No written customer reviews yet. Be the first to review this product!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (related.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text("More from this seller", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                related.forEach { rel ->
                    Spacer(Modifier.height(8.dp))
                    ProductCard(
                        product = rel,
                        vendorName = viewModel.getVendor(rel.vendorId)?.name ?: "",
                        isWishlisted = wishlist.contains(rel.id),
                        onClick = { onProductClick(rel.id) },
                        onAddToCart = {
                            scope.launch {
                                AppContainer.repository.addToCart(rel.id, 1)
                                snackbar.showSnackbar("Added ${rel.name} to cart")
                            }
                        },
                        onWishlistToggle = { scope.launch { AppContainer.repository.toggleWishlist(rel.id) } }
                    )
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }

    if (showReviewDialog) {
        WriteReviewDialog(
            productName = product.name,
            onDismiss = { showReviewDialog = false },
            onSubmit = { stars, rTitle, comment, name ->
                viewModel.submitReview(stars, rTitle, comment, name)
                showReviewDialog = false
                scope.launch { snackbar.showSnackbar("Review submitted! Thank you ⭐") }
            }
        )
    }
}
