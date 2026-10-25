package com.example.multivendor.data.local

import com.example.multivendor.data.Category
import com.example.multivendor.data.Order
import com.example.multivendor.data.Product
import com.example.multivendor.data.Review
import com.example.multivendor.data.Vendor

// ---- Product ----
fun Product.toEntity() = ProductEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    originalPrice = originalPrice,
    rating = rating,
    reviewsCount = reviews,
    categoryId = categoryId,
    vendorId = vendorId,
    stock = stock,
    isFeatured = isFeatured,
    isNew = isNew,
    tags = tags,
    gradientSeed = gradientSeed,
    imageUrl = imageUrl
)

fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    originalPrice = originalPrice,
    rating = rating,
    reviews = reviewsCount,
    categoryId = categoryId,
    vendorId = vendorId,
    stock = stock,
    isFeatured = isFeatured,
    isNew = isNew,
    tags = tags,
    gradientSeed = gradientSeed,
    imageUrl = imageUrl
)

// ---- Vendor ----
fun Vendor.toEntity() = VendorEntity(
    id = id,
    name = name,
    description = description,
    rating = rating,
    location = location,
    isVerified = isVerified,
    joinedYear = joinedYear,
    responseTime = responseTime,
    totalSales = totalSales
)

fun VendorEntity.toDomain() = Vendor(
    id = id,
    name = name,
    description = description,
    rating = rating,
    location = location,
    isVerified = isVerified,
    joinedYear = joinedYear,
    responseTime = responseTime,
    totalSales = totalSales
)

// ---- Category ----
fun Category.toEntity() = CategoryEntity(id = id, name = name, emoji = emoji)
fun CategoryEntity.toDomain() = Category(id = id, name = name, emoji = emoji)

// ---- Order ----
fun Order.toEntity(createdAt: Long = System.currentTimeMillis()) = OrderEntity(
    id = id,
    subtotal = subtotal,
    discount = discount,
    shipping = shipping,
    total = total,
    status = status,
    date = date,
    address = address,
    paymentMethod = paymentMethod,
    promoCode = promoCode,
    lines = lines,
    createdAt = createdAt
)

fun OrderEntity.toDomain() = Order(
    id = id,
    lines = lines,
    subtotal = subtotal,
    discount = discount,
    shipping = shipping,
    total = total,
    status = status,
    date = date,
    address = address,
    paymentMethod = paymentMethod,
    promoCode = promoCode
)

// ---- Review ----
fun Review.toEntity() = ReviewEntity(
    id = id,
    productId = productId,
    userName = userName,
    rating = rating,
    title = title,
    comment = comment,
    date = date,
    isVerifiedBuyer = isVerifiedBuyer,
    helpfulCount = helpfulCount
)

fun ReviewEntity.toDomain() = Review(
    id = id,
    productId = productId,
    userName = userName,
    rating = rating,
    title = title,
    comment = comment,
    date = date,
    isVerifiedBuyer = isVerifiedBuyer,
    helpfulCount = helpfulCount
)
