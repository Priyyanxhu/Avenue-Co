package com.example.multivendor.ui.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.auth.User
import com.example.multivendor.ui.cart.CartViewModel
import com.example.multivendor.ui.components.AvenueTopBar
import com.example.multivendor.ui.components.NotificationCenterDialog
import com.example.multivendor.ui.orders.OrdersViewModel
import com.example.multivendor.ui.wishlist.WishlistViewModel

@Composable
fun ProfileScreen(
    onOrdersClick: () -> Unit,
    onWishlistClick: () -> Unit,
    onCartClick: () -> Unit,
    onVendorClick: (String) -> Unit,
    onSellerDashboardClick: (String?) -> Unit = {},
    user: User? = null,
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    cartViewModel: CartViewModel = viewModel { CartViewModel(AppContainer.repository) },
    wishlistViewModel: WishlistViewModel = viewModel { WishlistViewModel(AppContainer.repository) },
    ordersViewModel: OrdersViewModel = viewModel { OrdersViewModel(AppContainer.repository) }
) {
    val cartState by cartViewModel.uiState.collectAsState()
    val wishlist by wishlistViewModel.uiState.collectAsState()
    val orders by ordersViewModel.orders.collectAsState()

    val userName = user?.name ?: "Priyanshu Pandey"
    val userEmail = user?.email ?: "pandey.shreyansh@example.com"
    val userPhone = user?.phone?.takeIf { it.isNotBlank() } ?: "+91 98765 43210"

    var showAddressDialog by remember { mutableStateOf(false) }
    var showWalletDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // 1. Top Bar: Avenue & Co.
        AvenueTopBar(
            onNotificationClick = { showNotificationsDialog = true },
            onProfileClick = {}
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Avenue Identity Header: Overline + Title + Settings Cog
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "AVENUE IDENTITY",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Account & Merchant\nHub",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F2557),
                            letterSpacing = (-0.4).sp,
                            lineHeight = 30.sp
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFEFF6FF),
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { showSettingsDialog = true }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = Color(0xFF0F2557),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // 3. User Identity Card (Photo, Name, Prime, Contact, VIP Tier)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar with verified badge
                            Box(modifier = Modifier.size(64.dp)) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF0F2557),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = userName.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").take(2),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.Verified,
                                            contentDescription = "Verified",
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userName,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F2557),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFEFF6FF),
                                        border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("⭐ ", fontSize = 10.sp)
                                            Text(
                                                text = "Prime",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E3A8A)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Mail, null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = userEmail,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Phone, null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = userPhone,
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // VIP Tier Banner
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "• Tier 3 VIP Verified • Avenue Perks Active",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E3A8A)
                                )
                                Text("🏅", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // 4. Quick Stat Counters (In Cart, Wishlist, Orders)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val inCartCount = cartState.lines.sumOf { it.quantity }.coerceAtLeast(2)
                    val savedCount = wishlist.size.coerceAtLeast(8)
                    val ordersCount = orders.size.coerceAtLeast(3)

                    QuickStatCard(
                        icon = Icons.Filled.ShoppingBag,
                        count = "$inCartCount Items",
                        label = "IN CART",
                        onClick = onCartClick,
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        icon = Icons.Filled.Favorite,
                        count = "$savedCount Saved",
                        label = "WISHLIST",
                        onClick = onWishlistClick,
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        icon = Icons.Filled.LocalShipping,
                        count = "$ordersCount Active",
                        label = "ORDERS",
                        onClick = onOrdersClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 5. Dark Seller Portal Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF0F2557),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "SELLER PORTAL & HUB • MERCHANT",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE2E8F0),
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Online",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }

                        Text(
                            text = "Manage product listings, inventory sync, and fulfill customer orders across your active Avenue stores.",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 17.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = null,
                                    tint = Color(0xFFE2E8F0),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Connected • 14 pending dispatches today",
                                    fontSize = 12.sp,
                                    color = Color(0xFFF1F5F9),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Button(
                            onClick = { onSellerDashboardClick("techhub") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF0F2557)
                            ),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                text = "OPEN SELLER DASHBOARD 🚀",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 6. Section: Your Managed Stores
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Your Managed Stores",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F2557)
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                        ) {
                            Text(
                                text = "1 Store Online",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // TechHub Managed Store Card
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF0F2557),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "TH",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "TechHub Store",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F2557)
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
                                        text = "Bengaluru • Urban Electronics & M...",
                                        fontSize = 11.5.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFDBEAFE))
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E3A8A),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // 3-Column Metrics Container (Rating 4.8 ⭐ | Items 142 | Dispatch 99.4%)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Rating", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Spacer(Modifier.height(2.dp))
                                        Text("4.8 ⭐", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2557))
                                    }
                                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color(0xFFCBD5E1)))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Items", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Spacer(Modifier.height(2.dp))
                                        Text("142", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2557))
                                    }
                                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color(0xFFCBD5E1)))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Dispatch", fontSize = 11.sp, color = Color(0xFF64748B))
                                        Spacer(Modifier.height(2.dp))
                                        Text("99.4%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2557))
                                    }
                                }
                            }

                            // Action buttons: [Catalog] [Preview]
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSellerDashboardClick("techhub") }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🗂️ ", fontSize = 12.sp)
                                        Text(
                                            text = "Catalog",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E3A8A)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onVendorClick("techhub") }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("👁️ ", fontSize = 12.sp)
                                        Text(
                                            text = "Preview",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E3A8A)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 7. Section: Preferences & Help
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Preferences & Help",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F2557)
                    )

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            PreferenceItem(
                                icon = Icons.Filled.LocationOn,
                                title = "Saved Delivery Addresses",
                                subtitle = "Home, Studio & TechHub HQ",
                                onClick = { showAddressDialog = true }
                            )
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF1F5F9)))
                            PreferenceItem(
                                icon = Icons.Filled.CreditCard,
                                title = "Payment Methods & Wallet",
                                subtitle = "UPI linked, 2 cards stored",
                                onClick = { showWalletDialog = true }
                            )
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF1F5F9)))
                            PreferenceItem(
                                icon = Icons.Filled.HeadsetMic,
                                title = "Help Center & 24/7 Support",
                                subtitle = "Instant dispute resolution & FAQs",
                                onClick = { showHelpDialog = true }
                            )
                        }
                    }
                }
            }

            // 8. Log Out Button (Soft Peach/Pink)
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEE2E2),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogoutClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 13.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Log Out of Avenue & Co.",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }

            // 9. Footer: App version & ecosystem tagline
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "AVENUE & CO. MOBILE HUB • VERSION 4.12.0",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Curated Multi-Vendor Commerce Ecosystem",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }

    if (showAddressDialog) {
        SavedAddressesDialog(
            onDismiss = { showAddressDialog = false },
            onAddressSelected = { /* address saved */ }
        )
    }
    if (showWalletDialog) {
        WalletAndPaymentsDialog(
            onDismiss = { showWalletDialog = false }
        )
    }
    if (showHelpDialog) {
        HelpSupportDialog(
            onDismiss = { showHelpDialog = false }
        )
    }
    if (showSettingsDialog) {
        AccountSettingsDialog(
            onDismiss = { showSettingsDialog = false }
        )
    }
    if (showNotificationsDialog) {
        NotificationCenterDialog(
            onDismiss = { showNotificationsDialog = false },
            onNavigateToOrders = onOrdersClick
        )
    }
}

@Composable
fun QuickStatCard(
    icon: ImageVector,
    count: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF0F2557),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = count,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F2557)
            )
            Text(
                text = label,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun PreferenceItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFEFF6FF),
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF1E3A8A),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F2557)
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = Color(0xFF64748B)
            )
        }

        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(18.dp)
        )
    }
}
