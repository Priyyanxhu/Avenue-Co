package com.example.multivendor.data

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val rating: Double = 4.0,
    val reviews: Int = 0,
    val categoryId: String,
    val vendorId: String,
    val stock: Int = 20,
    val isFeatured: Boolean = false,
    val isNew: Boolean = false,
    val tags: List<String> = emptyList(),
    val gradientSeed: Int = 0,
    val imageUrl: String? = null
) {
    val discountPercent: Int?
        get() = if (originalPrice != null && originalPrice > price) {
            (((originalPrice - price) / originalPrice) * 100).toInt()
        } else null

    val isOnSale: Boolean get() = discountPercent != null
}

@Serializable
data class Vendor(
    val id: String,
    val name: String,
    val description: String,
    val rating: Double,
    val location: String,
    val isVerified: Boolean = false,
    val joinedYear: Int = 2022,
    val responseTime: String = "~2 hrs",
    val totalSales: Int = 0
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val emoji: String
)

data class CartLine(
    val product: Product,
    val quantity: Int
) {
    val lineTotal: Double get() = product.price * quantity
}

@Serializable
data class Order(
    val id: String,
    val lines: List<OrderLine>,
    val subtotal: Double,
    val discount: Double,
    val shipping: Double,
    val total: Double,
    val status: String,
    val date: String,
    val address: String,
    val paymentMethod: String,
    val promoCode: String? = null
)

@Serializable
data class OrderLine(
    val productId: String,
    val productName: String,
    val unitPrice: Double,
    val quantity: Int,
    val vendorId: String = ""
)

enum class SortOption(val label: String) {
    POPULAR("Popular"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    RATING("Top Rated"),
    NEWEST("Newest")
}

@Serializable
data class Review(
    val id: String,
    val productId: String,
    val userName: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val date: String,
    val isVerifiedBuyer: Boolean = true,
    val helpfulCount: Int = 0
)

data class RatingBreakdown(
    val averageRating: Double,
    val totalReviews: Int,
    val starCounts: Map<Int, Int> = emptyMap()
) {
    fun percentageFor(star: Int): Float =
        if (totalReviews > 0) (starCounts[star] ?: 0).toFloat() / totalReviews else 0f
}
