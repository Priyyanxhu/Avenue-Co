package com.example.multivendor.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.multivendor.ui.components.formatPrice
import java.util.UUID

// ---------------------------------------------------------------------------
// 1. SAVED ADDRESSES DIALOG
// ---------------------------------------------------------------------------
data class UserAddress(
    val id: String,
    val title: String,
    val recipient: String,
    val fullAddress: String,
    val isDefault: Boolean = false
)

private val sampleAddresses = listOf(
    UserAddress(
        id = "addr-1",
        title = "Home",
        recipient = "Priya Pandey",
        fullAddress = "Flat 402, Prestige Elm, 12th Main, Indiranagar, Bengaluru - 560038",
        isDefault = true
    ),
    UserAddress(
        id = "addr-2",
        title = "Studio",
        recipient = "Priya Pandey (TechHub)",
        fullAddress = "80 Feet Road, 4th Block, Koramangala, Bengaluru - 560034",
        isDefault = false
    ),
    UserAddress(
        id = "addr-3",
        title = "Office",
        recipient = "Priyanshu Pandey",
        fullAddress = "Avenue Commerce Tower, 5th Floor, EPIP Zone, Whitefield, Bengaluru - 560066",
        isDefault = false
    )
)

@Composable
fun SavedAddressesDialog(
    onDismiss: () -> Unit,
    onAddressSelected: (String) -> Unit
) {
    val addresses = remember { mutableStateListOf(*sampleAddresses.toTypedArray()) }
    var showAddForm by remember { mutableStateOf(false) }

    var newTitle by remember { mutableStateOf("Home") }
    var newRecipient by remember { mutableStateOf("") }
    var newStreet by remember { mutableStateOf("") }
    var newCity by remember { mutableStateOf("Bengaluru") }
    var newPincode by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF8FAFC),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0F2557),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Saved Delivery Addresses", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2557))
                            Text("Manage shipping destinations", fontSize = 11.5.sp, color = Color(0xFF64748B))
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Addresses List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (showAddForm) 220.dp else 340.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(addresses, key = { it.id }) { addr ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, if (addr.isDefault) Color(0xFF2563EB) else Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAddressSelected(addr.fullAddress)
                                    onDismiss()
                                }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (addr.title == "Home") Icons.Filled.Home else Icons.Filled.Work,
                                            contentDescription = null,
                                            tint = Color(0xFF0F2557),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(addr.title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color(0xFF0F2557))
                                        if (addr.isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFEFF6FF),
                                                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                                            ) {
                                                Text(
                                                    "DEFAULT",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1E3A8A),
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (!addr.isDefault && addresses.size > 1) {
                                        IconButton(
                                            onClick = { addresses.remove(addr) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(15.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(addr.recipient, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                                Text(addr.fullAddress, fontSize = 11.5.sp, color = Color(0xFF64748B), lineHeight = 16.sp)
                            }
                        }
                    }
                }

                // Add New Address Form Section
                AnimatedVisibility(visible = showAddForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Add New Destination", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2557))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Home", "Work", "Other").forEach { tag ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (newTitle == tag) Color(0xFF0F2557) else Color.White,
                                    border = BorderStroke(1.dp, if (newTitle == tag) Color(0xFF0F2557) else Color(0xFFCBD5E1)),
                                    modifier = Modifier.clickable { newTitle = tag }
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (newTitle == tag) Color.White else Color(0xFF475569),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = newRecipient,
                            onValueChange = { newRecipient = it },
                            placeholder = { Text("Recipient Name & Phone", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                        )
                        OutlinedTextField(
                            value = newStreet,
                            onValueChange = { newStreet = it },
                            placeholder = { Text("Flat / House No., Street, Area", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = newCity,
                                onValueChange = { newCity = it },
                                placeholder = { Text("City", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                            )
                            OutlinedTextField(
                                value = newPincode,
                                onValueChange = { newPincode = it },
                                placeholder = { Text("Pincode", fontSize = 12.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                            )
                        }

                        Button(
                            onClick = {
                                if (newStreet.isNotBlank() && newRecipient.isNotBlank()) {
                                    val formatted = "${newStreet.trim()}, ${newCity.trim()} - ${newPincode.trim()}"
                                    addresses.add(
                                        UserAddress(
                                            id = "addr-" + UUID.randomUUID().toString().take(6),
                                            title = newTitle,
                                            recipient = newRecipient.trim(),
                                            fullAddress = formatted
                                        )
                                    )
                                    showAddForm = false
                                    newStreet = ""
                                    newRecipient = ""
                                    newPincode = ""
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2557)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Address", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (!showAddForm) {
                    OutlinedButton(
                        onClick = { showAddForm = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("+ Add New Address", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 2. PAYMENT METHODS & WALLET DIALOG
// ---------------------------------------------------------------------------
@Composable
fun WalletAndPaymentsDialog(
    onDismiss: () -> Unit
) {
    var walletBalance by remember { mutableDoubleStateOf(2450.0) }
    var showAddMoneyToast by remember { mutableStateOf(false) }

    val upiHandles = remember { mutableStateListOf("priya@okhdfcbank", "techhub@icici") }
    var newUpiInput by remember { mutableStateOf("") }
    var showUpiField by remember { mutableStateOf(false) }

    val savedCards = remember {
        mutableStateListOf(
            Pair("HDFC Millennia Credit Card", "•••• 4242 (Expires 08/29)"),
            Pair("ICICI Amazon Pay Card", "•••• 8819 (Expires 11/30)")
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF8FAFC),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0F2557),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Avenue Wallet & Cards", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2557))
                            Text("1-tap checkout balance & saved methods", fontSize = 11.5.sp, color = Color(0xFF64748B))
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wallet Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF0F2557),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF0F2557), Color(0xFF1E3A8A), Color(0xFF2563EB))
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("AVENUE CASH WALLET", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF93C5FD), letterSpacing = 1.sp)
                                Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.15f)) {
                                    Text("⭐ VIP 5% Cashback", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(formatPrice(walletBalance), fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Usable on any multi-vendor order with zero processing fee", fontSize = 11.sp, color = Color(0xFFE2E8F0))

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(500.0, 1000.0, 2000.0).forEach { amount ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White.copy(alpha = 0.2f),
                                        modifier = Modifier.clickable {
                                            walletBalance += amount
                                            showAddMoneyToast = true
                                        }
                                    ) {
                                        Text(
                                            "+${formatPrice(amount)}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (showAddMoneyToast) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("✓ Added funds to wallet successfully!", fontSize = 11.5.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Saved UPI Section
                Text("LINKED UPI HANDLES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 0.8.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    upiHandles.forEach { handle ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("⚡ ", fontSize = 13.sp)
                                    Text(handle, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2557))
                                }
                                Text("Verified", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                            }
                        }
                    }

                    if (showUpiField) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newUpiInput,
                                onValueChange = { newUpiInput = it },
                                placeholder = { Text("username@upi", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                            )
                            Button(
                                onClick = {
                                    if (newUpiInput.contains("@")) {
                                        upiHandles.add(newUpiInput.trim())
                                        newUpiInput = ""
                                        showUpiField = false
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2557))
                            ) {
                                Text("Link", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Text(
                            text = "+ Link new UPI ID",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2563EB),
                            modifier = Modifier.clickable { showUpiField = true }.padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Saved Cards Section
                Text("STORED CARDS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 0.8.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    savedCards.forEach { card ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.CreditCard, contentDescription = null, tint = Color(0xFF0F2557), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(card.first, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2557))
                                    Text(card.second, fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2557)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. HELP CENTER & LIVE SUPPORT CONCIERGE DIALOG
// ---------------------------------------------------------------------------
data class SupportMessage(val id: String, val sender: String, val text: String, val isUser: Boolean)

@Composable
fun HelpSupportDialog(
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var chatInput by remember { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf(
            SupportMessage("m1", "Avenue Concierge", "Hello Priya! 👋 I'm your Avenue Concierge. How can I help you with your multi-vendor orders, returns, or seller inquiries?", false)
        )
    }

    val faqs = listOf(
        Pair("📦 Multi-Vendor Delivery Policy", "Orders with items from multiple independent makers are dispatched directly from each vendor's studio to ensure maximum freshness and craftsmanship."),
        Pair("🔄 7-Day Hassle-Free Returns", "Any item can be returned within 7 days of delivery. Refunds are instantly credited to your Avenue Cash Wallet upon pickup verification."),
        Pair("🛡️ Verified Artisan & Brand Guarantee", "Every merchant on Avenue undergoes strict authentication. 100% genuine products with manufacturer warranty coverage."),
        Pair("⚡ 20M Express Delivery Pincodes", "Express 20-minute delivery is active across select urban clusters in Indiranagar, Koramangala, and Powai.")
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF8FAFC),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0F2557),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.HeadsetMic, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Avenue Support Concierge", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2557))
                            Text("24/7 Priority Resolution", fontSize = 11.5.sp, color = Color(0xFF64748B))
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs: [Instant FAQs] [Live Concierge Chat]
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF0F2557)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("FAQs & Policies", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium, fontSize = 12.5.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Live Concierge 💬", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 12.5.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(faqs) { (q, a) ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(q, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F2557))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(a, fontSize = 11.5.sp, color = Color(0xFF475569), lineHeight = 16.sp)
                                }
                            }
                        }
                    }
                } else {
                    // Chat Interface
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(messages, key = { it.id }) { msg ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(
                                            topStart = 14.dp,
                                            topEnd = 14.dp,
                                            bottomStart = if (msg.isUser) 14.dp else 2.dp,
                                            bottomEnd = if (msg.isUser) 2.dp else 14.dp
                                        ),
                                        color = if (msg.isUser) Color(0xFF0F2557) else Color.White,
                                        border = if (msg.isUser) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth(0.85f)
                                    ) {
                                        Text(
                                            text = msg.text,
                                            color = if (msg.isUser) Color.White else Color(0xFF1E293B),
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Where is my order?", "Request return").forEach { chip ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFEFF6FF),
                                    border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                                    modifier = Modifier.clickable {
                                        messages.add(SupportMessage("user-" + UUID.randomUUID(), "Priya", chip, true))
                                        val reply = if (chip.contains("order")) {
                                            "Order #ORD-78A1B2 is currently with BlueDart express out for delivery in Indiranagar! Delivery partner: Ramesh (+91 98451 22301)."
                                        } else {
                                            "You can initiate a 1-tap return directly on any delivered order in your Orders tab. Refunds go instantly to Avenue Wallet."
                                        }
                                        messages.add(SupportMessage("bot-" + UUID.randomUUID(), "Avenue Concierge", reply, false))
                                    }
                                ) {
                                    Text(chip, fontSize = 10.5.sp, color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Text input row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Ask anything...", fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White)
                            )
                            IconButton(
                                onClick = {
                                    if (chatInput.isNotBlank()) {
                                        val q = chatInput.trim()
                                        messages.add(SupportMessage("user-" + UUID.randomUUID(), "Priya", q, true))
                                        chatInput = ""
                                        messages.add(
                                            SupportMessage(
                                                "bot-" + UUID.randomUUID(),
                                                "Avenue Concierge",
                                                "Thank you for contacting Avenue Priority Desk! Our concierge agent has flagged your query regarding '$q' and resolved with merchant warranty.",
                                                false
                                            )
                                        )
                                    }
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFF0F2557))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 4. ACCOUNT SETTINGS DIALOG (Cog Icon)
// ---------------------------------------------------------------------------
@Composable
fun AccountSettingsDialog(
    onDismiss: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var smsTrackingEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(false) }
    var darkModePref by remember { mutableStateOf(false) }
    var cacheCleared by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Account Preferences", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F2557))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggles
                SettingToggleRow(
                    title = "Push Notifications",
                    subtitle = "Real-time dispatch & price drop alerts",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SettingToggleRow(
                    title = "SMS Order Tracking",
                    subtitle = "OTP delivery codes sent to phone",
                    checked = smsTrackingEnabled,
                    onCheckedChange = { smsTrackingEnabled = it }
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SettingToggleRow(
                    title = "Biometric Lock",
                    subtitle = "Require fingerprint / FaceID on checkout",
                    checked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it }
                )
                HorizontalDivider(color = Color(0xFFF1F5F9))
                SettingToggleRow(
                    title = "AMOLED Dark Mode",
                    subtitle = "Sleek low-light aesthetic",
                    checked = darkModePref,
                    onCheckedChange = { darkModePref = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { cacheCleared = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (cacheCleared) "✓ Local Cache Purged (0 KB)" else "Clear Local Cache & Image Temp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F2557)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save & Close", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F2557))
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0F2557))
        )
    }
}
