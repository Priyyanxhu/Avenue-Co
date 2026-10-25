package com.example.multivendor

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey

@Serializable data object Login : NavKey

@Serializable data object Register : NavKey

@Serializable data object Shop : NavKey

@Serializable data object VendorList : NavKey

@Serializable data object Wishlist : NavKey

@Serializable data object Cart : NavKey

@Serializable data object Profile : NavKey

@Serializable data object Orders : NavKey

@Serializable data object Checkout : NavKey

@Serializable data class ProductDetail(val productId: String) : NavKey

@Serializable data class VendorDetail(val vendorId: String) : NavKey

@Serializable data class OrderSuccess(val orderId: String) : NavKey

@Serializable data class SellerDashboard(val vendorId: String? = null) : NavKey

@Serializable data class SellerAddEditProduct(val vendorId: String, val productId: String? = null) : NavKey

@Serializable data class SellerOrders(val vendorId: String) : NavKey

@Serializable data class CategoryCatalog(val categoryId: String? = null) : NavKey
