package com.example.multivendor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.multivendor.theme.BrandOrange
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.multivendor.data.AppContainer
import com.example.multivendor.ui.auth.AuthViewModel
import com.example.multivendor.ui.auth.LoginScreen
import com.example.multivendor.ui.auth.RegisterScreen
import com.example.multivendor.ui.cart.CartScreen
import com.example.multivendor.ui.cart.CartViewModel
import com.example.multivendor.ui.checkout.CheckoutScreen
import com.example.multivendor.ui.checkout.OrderSuccessScreen
import com.example.multivendor.ui.components.AppLogoMark
import com.example.multivendor.ui.orders.OrdersScreen
import com.example.multivendor.ui.product.ProductDetailScreen
import com.example.multivendor.ui.profile.ProfileScreen
import com.example.multivendor.ui.shop.ShopScreen
import com.example.multivendor.ui.shop.CategoryCatalogScreen
import com.example.multivendor.ui.seller.SellerAddEditProductScreen
import com.example.multivendor.ui.seller.SellerDashboardScreen
import com.example.multivendor.ui.seller.SellerOrdersScreen
import com.example.multivendor.ui.vendor.VendorDetailScreen
import com.example.multivendor.ui.vendor.VendorListScreen
import com.example.multivendor.ui.wishlist.WishlistScreen

private val TopLevelDestinations = listOf(Shop, VendorList, Wishlist, Cart, Profile)

private fun NavKey.isTopLevel(): Boolean = this in TopLevelDestinations

/**
 * Root navigation with auth gating:
 * splash while the persisted session loads -> login/register when logged out ->
 * marketplace tabs when logged in.
 */
@Composable
fun MainNavigation() {
    val auth = remember { runCatching { AppContainer.authRepository }.getOrNull() }
    if (auth == null) {
        // Previews / unit tests: AppContainer.init() was never called.
        MarketplaceNavigation()
        return
    }
    val ready by auth.sessionReady.collectAsState(initial = false)
    val loggedIn by auth.isLoggedIn.collectAsState(initial = false)
    when {
        !ready -> SplashScreen()
        !loggedIn -> AuthNavigation()
        else -> MarketplaceNavigation()
    }
}

@Composable
private fun SplashScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AppLogoMark(size = 72)
            Spacer(Modifier.height(20.dp))
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun AuthNavigation() {
    val backStack = rememberNavBackStack(Login)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Login> {
                LoginScreen(
                    onRegisterClick = { backStack.add(Register) },
                    onLoginSuccess = { /* auth gating flips automatically */ }
                )
            }
            entry<Register> {
                RegisterScreen(
                    onBackToLogin = { backStack.removeLastOrNull() },
                    onRegisterSuccess = { /* auth gating flips automatically */ }
                )
            }
        }
    )
}

@Composable
private fun MarketplaceNavigation() {
    val backStack = rememberNavBackStack(Shop)
    val cartViewModel: CartViewModel = viewModel { CartViewModel(AppContainer.repository) }
    val cartCount by cartViewModel.uiState.collectAsState()

    fun navigateTo(key: NavKey) {
        if (key.isTopLevel()) {
            // Single-task tabs: keep only the selected top-level destination.
            backStack.clear()
            backStack.add(key)
        } else {
            backStack.add(key)
        }
    }

    fun navigateSingleTop(key: NavKey) {
        backStack.removeAll { it == key }
        backStack.add(key)
    }

    val current = backStack.lastOrNull()
    val showBottomBar = current != null && (current.isTopLevel() || current == Orders || current is CategoryCatalog)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                val navItemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF0F2557),
                    selectedTextColor = Color(0xFF0F2557),
                    indicatorColor = Color(0xFFEFF6FF),
                    unselectedIconColor = Color(0xFF64748B),
                    unselectedTextColor = Color(0xFF64748B)
                )
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 2.dp
                ) {
                    NavigationBarItem(
                        selected = current == Shop || current == Main || current is CategoryCatalog,
                        onClick = { navigateTo(Shop) },
                        icon = { Icon(if (current == Shop || current == Main || current is CategoryCatalog) Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                        label = { Text("Home", fontWeight = if (current == Shop || current == Main || current is CategoryCatalog) FontWeight.Bold else FontWeight.Medium, style = MaterialTheme.typography.labelSmall) },
                        colors = navItemColors,
                        alwaysShowLabel = true
                    )
                    NavigationBarItem(
                        selected = current == VendorList,
                        onClick = { navigateTo(VendorList) },
                        icon = { Icon(if (current == VendorList) Icons.Filled.Store else Icons.Outlined.Store, contentDescription = "Sellers") },
                        label = { Text("Sellers", fontWeight = if (current == VendorList) FontWeight.Bold else FontWeight.Medium, style = MaterialTheme.typography.labelSmall) },
                        colors = navItemColors,
                        alwaysShowLabel = true
                    )
                    NavigationBarItem(
                        selected = current == Wishlist,
                        onClick = { navigateTo(Wishlist) },
                        icon = { Icon(if (current == Wishlist) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = "Wishlist") },
                        label = { Text("Wishlist", fontWeight = if (current == Wishlist) FontWeight.Bold else FontWeight.Medium, style = MaterialTheme.typography.labelSmall) },
                        colors = navItemColors,
                        alwaysShowLabel = true
                    )
                    NavigationBarItem(
                        selected = current == Cart,
                        onClick = { navigateTo(Cart) },
                        icon = {
                            val cartItems = cartCount.lines.sumOf { it.quantity }.coerceAtLeast(3)
                            BadgedBox(
                                badge = {
                                    Badge(containerColor = Color(0xFF0F2557), contentColor = Color.White) {
                                        Text("$cartItems", fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) { Icon(if (current == Cart) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart, contentDescription = "Cart") }
                        },
                        label = { Text("Cart", fontWeight = if (current == Cart) FontWeight.Bold else FontWeight.Medium, style = MaterialTheme.typography.labelSmall) },
                        colors = navItemColors,
                        alwaysShowLabel = true
                    )
                    NavigationBarItem(
                        selected = current == Profile || current == Orders,
                        onClick = { navigateTo(Profile) },
                        icon = { Icon(if (current == Profile || current == Orders) Icons.Filled.Person else Icons.Outlined.Person, contentDescription = "Profile") },
                        label = { Text("Profile", fontWeight = if (current == Profile || current == Orders) FontWeight.Bold else FontWeight.Medium, style = MaterialTheme.typography.labelSmall) },
                        colors = navItemColors,
                        alwaysShowLabel = true
                    )
                }
            }
        }
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(padding),
            onBack = { backStack.removeLastOrNull() },
            entryProvider = entryProvider {
                entry<Main> {
                    ShopScreen(
                        onProductClick = { id -> backStack.add(ProductDetail(id)) },
                        onVendorClick = { id -> backStack.add(VendorDetail(id)) },
                        onCategoryCatalogClick = { catId -> backStack.add(CategoryCatalog(catId)) },
                        onProfileClick = { navigateTo(Profile) }
                    )
                }
                entry<Shop> {
                    ShopScreen(
                        onProductClick = { id -> backStack.add(ProductDetail(id)) },
                        onVendorClick = { id -> backStack.add(VendorDetail(id)) },
                        onCategoryCatalogClick = { catId -> backStack.add(CategoryCatalog(catId)) },
                        onProfileClick = { navigateTo(Profile) }
                    )
                }
                entry<CategoryCatalog> { key ->
                    CategoryCatalogScreen(
                        initialCategoryId = key.categoryId,
                        onBack = { backStack.removeLastOrNull() },
                        onProductClick = { id -> backStack.add(ProductDetail(id)) },
                        onExploreMakersClick = { navigateTo(VendorList) },
                        onProfileClick = { navigateTo(Profile) }
                    )
                }
                entry<VendorList> {
                    VendorListScreen(
                        onVendorClick = { id -> backStack.add(VendorDetail(id)) },
                        onProfileClick = { navigateTo(Profile) },
                        onApplyVendorClick = { backStack.add(SellerDashboard("techhub")) }
                    )
                }
                entry<VendorDetail> { key ->
                    VendorDetailScreen(
                        vendorId = key.vendorId,
                        onBack = { backStack.removeLastOrNull() },
                        onProductClick = { id -> backStack.add(ProductDetail(id)) },
                        onManageStore = { id -> backStack.add(SellerDashboard(id)) }
                    )
                }
                entry<ProductDetail> { key ->
                    ProductDetailScreen(
                        productId = key.productId,
                        onBack = { backStack.removeLastOrNull() },
                        onProductClick = { id -> backStack.add(ProductDetail(id)) },
                        onVendorClick = { id -> backStack.add(VendorDetail(id)) },
                        onCartClick = { navigateTo(Cart) }
                    )
                }
                entry<Wishlist> {
                    WishlistScreen(
                        onProductClick = { id -> backStack.add(ProductDetail(id)) },
                        onBrowse = { navigateTo(Shop) }
                    )
                }
                entry<Cart> {
                    CartScreen(
                        onProductClick = { id -> backStack.add(ProductDetail(id)) },
                        onCheckout = { backStack.add(Checkout) },
                        onBrowse = { navigateTo(Shop) },
                        onProfileClick = { navigateTo(Profile) }
                    )
                }
                entry<Checkout> {
                    CheckoutScreen(
                        onBack = { backStack.removeLastOrNull() },
                        onOrderPlaced = { orderId ->
                            backStack.removeAll { it == Checkout || it == Cart }
                            backStack.add(OrderSuccess(orderId))
                        }
                    )
                }
                entry<OrderSuccess> { key ->
                    OrderSuccessScreen(
                        orderId = key.orderId,
                        onViewOrders = { navigateSingleTop(Orders) },
                        onContinue = { navigateTo(Shop) }
                    )
                }
                entry<Orders> {
                    OrdersScreen(
                        onBrowse = { navigateTo(Shop) },
                        onReordered = { navigateTo(Cart) }
                    )
                }
                entry<Profile> {
                    val authViewModel: AuthViewModel = viewModel { AuthViewModel() }
                    val user by authViewModel.currentUser.collectAsState(initial = null)
                    ProfileScreen(
                        onOrdersClick = { backStack.add(Orders) },
                        onWishlistClick = { navigateTo(Wishlist) },
                        onCartClick = { navigateTo(Cart) },
                        onVendorClick = { id -> backStack.add(VendorDetail(id)) },
                        onSellerDashboardClick = { vendorId -> backStack.add(SellerDashboard(vendorId)) },
                        user = user,
                        onLogoutClick = { authViewModel.logout() }
                    )
                }
                entry<SellerDashboard> { key ->
                    SellerDashboardScreen(
                        initialVendorId = key.vendorId,
                        onBack = { backStack.removeLastOrNull() },
                        onAddProduct = { vendorId -> backStack.add(SellerAddEditProduct(vendorId = vendorId)) },
                        onEditProduct = { vendorId, productId -> backStack.add(SellerAddEditProduct(vendorId = vendorId, productId = productId)) },
                        onViewOrders = { vendorId -> backStack.add(SellerOrders(vendorId = vendorId)) },
                        onProductClick = { productId -> backStack.add(ProductDetail(productId)) }
                    )
                }
                entry<SellerAddEditProduct> { key ->
                    SellerAddEditProductScreen(
                        vendorId = key.vendorId,
                        productId = key.productId,
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                entry<SellerOrders> { key ->
                    SellerOrdersScreen(
                        vendorId = key.vendorId,
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
            }
        )
    }
}
