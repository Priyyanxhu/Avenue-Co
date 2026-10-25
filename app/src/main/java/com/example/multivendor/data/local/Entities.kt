package com.example.multivendor.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.multivendor.data.OrderLine

/** Room entity for app users (login / registration). Passwords are never stored in plain text. */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double?,
    val rating: Double,
    val reviewsCount: Int,
    val categoryId: String,
    val vendorId: String,
    val stock: Int,
    val isFeatured: Boolean,
    val isNew: Boolean,
    val tags: List<String> = emptyList(),
    val gradientSeed: Int = 0,
    val imageUrl: String?
)

@Entity(tableName = "vendors")
data class VendorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val rating: Double,
    val location: String,
    val isVerified: Boolean,
    val joinedYear: Int,
    val responseTime: String,
    val totalSales: Int
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val emoji: String
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey val productId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val subtotal: Double,
    val discount: Double,
    val shipping: Double,
    val total: Double,
    val status: String,
    val date: String,
    val address: String,
    val paymentMethod: String,
    val promoCode: String?,
    val lines: List<OrderLine> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "reviews",
    indices = [Index(value = ["productId"])]
)
data class ReviewEntity(
    @PrimaryKey val id: String,
    val productId: String,
    val userName: String,
    val rating: Int,
    val title: String,
    val comment: String,
    val date: String,
    val isVerifiedBuyer: Boolean,
    val helpfulCount: Int
)
