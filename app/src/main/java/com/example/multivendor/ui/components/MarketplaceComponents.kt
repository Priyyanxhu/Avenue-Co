package com.example.multivendor.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.multivendor.data.Product
import com.example.multivendor.data.Vendor
import com.example.multivendor.theme.BrandOrange
import com.example.multivendor.theme.BrandOrangeTint
import com.example.multivendor.theme.StarAmber
import com.example.multivendor.theme.SuccessBg
import com.example.multivendor.theme.SuccessGreen
import com.example.multivendor.theme.WishlistPink
import com.example.multivendor.theme.WishlistPinkBg
import com.example.multivendor.theme.gradientFor
import java.util.Locale

fun formatPrice(value: Double): String = String.format(Locale.forLanguageTag("en-IN"), "₹%,.0f", value)

@Composable
fun ProductImageHeader(
    product: Product,
    modifier: Modifier = Modifier,
    height: Int = 140,
    showDiscount: Boolean = true,
    isWishlisted: Boolean = false,
    onWishlistClick: (() -> Unit)? = null
) {
    val (c1, c2) = gradientFor(product.gradientSeed)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Brush.linearGradient(listOf(c1, c2)))
    ) {
        // Emoji fallback stays behind the photo — visible while loading / on error.
        Text(
            text = productEmoji(product),
            modifier = Modifier.align(Alignment.Center),
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold
        )
        // Real product photo (Unsplash) on top of the gradient.
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
            // Bottom scrim for legibility of NEW pill over photos.
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.28f))
                        )
                    )
            )
        } else {
            // Soft decorative circles for depth when no photo.
            Box(
                Modifier
                    .size(120.dp)
                    .offset(x = (-28).dp, y = (-36).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f))
            )
            Box(
                Modifier
                    .size(84.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 22.dp, y = 26.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.10f))
            )
            // Bottom scrim for legibility of NEW pill.
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.14f))
                        )
                    )
            )
        }
        if (showDiscount && product.discountPercent != null) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
            ) {
                Text(
                    text = "-${product.discountPercent}%",
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = BrandOrange,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        if (onWishlistClick != null) {
            IconButton(
                onClick = onWishlistClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .shadow(4.dp, CircleShape)
                    .background(
                        if (isWishlisted) WishlistPinkBg else Color.White.copy(alpha = 0.96f),
                        CircleShape
                    )
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = if (isWishlisted) "Remove from wishlist" else "Add to wishlist",
                    tint = if (isWishlisted) WishlistPink else Color(0xFF475569),
                    modifier = Modifier.size(19.dp)
                )
            }
        }
        if (product.isNew) {
            Surface(
                color = SuccessGreen,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 3.dp,
                modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
            ) {
                Text(
                    text = "● NEW",
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

fun productEmoji(product: Product): String {
    return when (product.categoryId) {
        "electronics" -> if (product.name.contains("Watch")) "⌚" else if (product.name.contains("Keyboard")) "⌨️" else "🎧"
        "fashion" -> if (product.name.contains("Sneaker")) "👟" else if (product.name.contains("Jacket")) "🧥" else "👕"
        "home" -> if (product.name.contains("Coffee")) "☕" else if (product.name.contains("Lamp")) "💡" else "🛏️"
        "beauty" -> if (product.name.contains("Lipstick")) "💋" else "🧴"
        "sports" -> if (product.name.contains("Shoe")) "👟" else "🧘"
        "books" -> "📖"
        else -> "🛍️"
    }
}

@Composable
fun RatingStars(rating: Double, reviews: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = when {
                rating >= 4.0 -> SuccessGreen
                rating >= 3.0 -> StarAmber
                else -> MaterialTheme.colorScheme.error
            },
            shape = RoundedCornerShape(6.dp)
        ) {
            Row(
                Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "%.1f".format(rating),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(2.dp))
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
        Spacer(Modifier.width(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            val full = rating.toInt().coerceIn(0, 5)
            val half = (rating - full) >= 0.5
            repeat(full) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = StarAmber, modifier = Modifier.size(13.dp))
            }
            if (half && full < 5) {
                Icon(Icons.AutoMirrored.Filled.StarHalf, contentDescription = null, tint = StarAmber, modifier = Modifier.size(13.dp))
            }
            val remaining = 5 - full - (if (half) 1 else 0)
            repeat(remaining.coerceAtLeast(0)) {
                Icon(Icons.Filled.StarBorder, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(13.dp))
            }
        }
        Spacer(Modifier.width(5.dp))
        Text(
            text = "($reviews)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    vendorName: String,
    isWishlisted: Boolean,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onWishlistToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp, pressedElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column {
            ProductImageHeader(
                product = product,
                isWishlisted = isWishlisted,
                onWishlistClick = onWishlistToggle
            )
            Column(Modifier.padding(13.dp)) {
                Text(
                    text = vendorName.uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.4.sp
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(5.dp))
                RatingStars(product.rating, product.reviews)
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatPrice(product.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (product.originalPrice != null) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = formatPrice(product.originalPrice),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                if (product.stock <= 5) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = "Only ${product.stock} left in stock!",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 9.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(7.dp))
                    Text("Add to Cart", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun VendorCard(
    vendor: Vendor,
    productCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Store, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(vendor.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                    if (vendor.isVerified) {
                        Spacer(Modifier.width(5.dp))
                        Icon(Icons.Filled.Verified, contentDescription = "Verified", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(17.dp))
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$productCount products • ⭐ %.1f • %s".format(vendor.rating, vendor.location),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Surface(color = SuccessBg, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        "✓ Trusted • ${vendor.responseTime} response",
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null, actionLabel: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.2).sp)
            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)) {
                Text(actionLabel, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(2.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun QuantityStepper(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    max: Int = 99
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDecrease,
                enabled = quantity > 1,
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (quantity > 1) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
                        CircleShape
                    )
            ) {
                Icon(Icons.Filled.Remove, contentDescription = "Decrease quantity", modifier = Modifier.size(16.dp))
            }
            Text(
                text = quantity.toString(),
                modifier = Modifier.padding(horizontal = 14.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            IconButton(
                onClick = onIncrease,
                enabled = quantity < max,
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (quantity < max) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Increase quantity",
                    tint = if (quantity < max) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    subtitle: String,
    icon: ImageVector,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(42.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(18.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp)
            ) { Text(actionLabel, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(strokeWidth = 3.dp, modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(12.dp))
            Text("Loading…", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PromoBanner(title: String, subtitle: String, c1: Color, c2: Color, emoji: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(c1, c2)))
            .padding(20.dp)
    ) {
        // Decorative oversized circles.
        Box(
            Modifier
                .size(150.dp)
                .offset(x = 120.dp, y = (-70).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
        )
        Box(
            Modifier
                .size(90.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 30.dp, y = 40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.10f))
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Surface(color = Color.White.copy(alpha = 0.22f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        "LIMITED TIME",
                        Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(title, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, lineHeight = 25.sp)
                Spacer(Modifier.height(4.dp))
                Text(subtitle, color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White, shadowElevation = 3.dp) {
                    Text(
                        "Shop Now →",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = c2
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(emoji, fontSize = 54.sp, modifier = Modifier.padding(end = 4.dp))
        }
    }
}

@Composable
fun VerifiedBadge(modifier: Modifier = Modifier) {
    Surface(color = SuccessBg, shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Verified Seller", color = SuccessGreen, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TrustRow(modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("🚚" to "Free Ship ₹499+", "↩️" to "7-day Returns", "🛡️" to "1-yr Warranty").forEach { (e, t) ->
            AssistChip(
                onClick = {},
                label = { Text(t, style = MaterialTheme.typography.labelMedium) },
                leadingIcon = { Text(e) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SavingsPill(savingsText: String, modifier: Modifier = Modifier) {
    Surface(
        color = BrandOrangeTint,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BrandOrange.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Text(
            savingsText,
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = BrandOrange,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Small square product thumbnail with real photo + gradient/emoji fallback.
 * Used in cart lines, seller dashboard rows, order history, etc.
 */
@Composable
fun ProductThumbnail(
    product: Product,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 14,
    emojiFontSize: Int = 26
) {
    val (c1, c2) = gradientFor(product.gradientSeed)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(Brush.linearGradient(listOf(c1, c2))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = productEmoji(product),
            fontSize = emojiFontSize.sp,
            fontWeight = FontWeight.Bold
        )
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
}
