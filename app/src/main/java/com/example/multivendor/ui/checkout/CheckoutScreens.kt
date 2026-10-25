package com.example.multivendor.ui.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.MarketplaceRepository
import com.example.multivendor.theme.SuccessGreen
import com.example.multivendor.ui.cart.SummaryRow
import com.example.multivendor.ui.components.formatPrice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val repository: MarketplaceRepository = AppContainer.repository
) : ViewModel() {
    private val _promo = MutableStateFlow("")
    val promo = _promo.asStateFlow()

    val summary = combine(repository.cartLines, repository.cartSubtotal, _promo) { lines, subtotal, code ->
        val normalized = code.trim().uppercase().takeIf { it.isNotEmpty() }
        val discount = repository.discountForPromo(normalized, subtotal)
        val shipping = repository.shippingFor(subtotal - discount, normalized)
        CheckoutSummary(lines.size, subtotal, discount, shipping, (subtotal - discount + shipping).coerceAtLeast(0.0))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CheckoutSummary(0, 0.0, 0.0, 0.0, 0.0))

    val cartLines = repository.cartLines

    fun setPromo(v: String) { _promo.value = v }

    suspend fun placeOrder(address: String, payment: String): Result<String> {
        return try {
            val order = repository.placeOrder(address, payment, _promo.value.takeIf { it.isNotBlank() })
            Result.success(order.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class CheckoutSummary(val count: Int, val subtotal: Double, val discount: Double, val shipping: Double, val total: Double)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CheckoutViewModel = viewModel { CheckoutViewModel(AppContainer.repository) }
) {
    val summary by viewModel.summary.collectAsState()

    val savedAddresses = remember {
        listOf(
            Triple("Home (Default)", "Priya Sharma • +91 98765 43210", "42, MG Road, Indiranagar, Bengaluru - 560038"),
            Triple("Work / Office", "Priya Sharma • +91 98765 43210", "Manyata Tech Park, Block G3, Nagavara, Bengaluru - 560045"),
            Triple("Family Home", "Priya Sharma • +91 98765 43210", "Sector 15, H-21, Noida, Uttar Pradesh - 201301")
        )
    }
    var selectedAddressIndex by remember { mutableStateOf(0) }
    var useCustomAddress by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("Priya Sharma") }
    var phone by remember { mutableStateOf("9876543210") }
    var address by remember { mutableStateOf("42, MG Road, Indiranagar") }
    var city by remember { mutableStateOf("Bengaluru") }
    var pincode by remember { mutableStateOf("560038") }
    var payment by remember { mutableStateOf("UPI (Google Pay / PhonePe)") }
    var promoInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var placing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val currentFinalAddress = if (useCustomAddress) {
        "$name, $address, $city - $pincode (Ph: $phone)"
    } else {
        "${savedAddresses[selectedAddressIndex].second}, ${savedAddresses[selectedAddressIndex].third}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout 🛒", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Delivery Address Card with Saved Options
            ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Delivery Address 📍", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = { useCustomAddress = !useCustomAddress }) {
                            Text(if (useCustomAddress) "Use Saved" else "+ Add New")
                        }
                    }

                    if (!useCustomAddress) {
                        savedAddresses.forEachIndexed { index, (label, contact, addr) ->
                            val isSelected = selectedAddressIndex == index
                            Surface(
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedAddressIndex = index }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Text(contact, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(addr, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(name, { name = it; error = null }, label = { Text("Full name *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(phone, { phone = it.filter { c -> c.isDigit() }.take(10); error = null }, label = { Text("Phone (10 digits) *") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(address, { address = it; error = null }, label = { Text("Flat / House No. & Street *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(city, { city = it; error = null }, label = { Text("City *") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                            OutlinedTextField(pincode, { pincode = it.filter { c -> c.isDigit() }.take(6); error = null }, label = { Text("Pincode *") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                        }
                    }
                }
            }

            // Payment Methods Card
            ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Payment Method 💳", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    listOf(
                        "Avenue Cash Wallet" to "Balance: ₹2,450.00 • Instant 1-tap checkout ⚡",
                        "UPI (Google Pay / PhonePe)" to "Instant & Secure ⚡",
                        "Credit / Debit Card" to "Visa, MasterCard, RuPay 💳",
                        "Net Banking" to "All Indian Banks 🏦",
                        "Cash on Delivery" to "Pay cash or UPI at delivery 💵"
                    ).forEach { (method, subtitle) ->
                        val isSelected = payment == method
                        Surface(
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { payment = method }
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (isSelected) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
                                    null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text(method, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                    Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // Promo Code & Price Summary
            ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Coupons & Offers 🏷️", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = promoInput,
                            onValueChange = { promoInput = it },
                            placeholder = { Text("Enter coupon code") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Button(
                            onClick = { viewModel.setPromo(promoInput) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Apply")
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        androidx.compose.material3.AssistChip(
                            onClick = {
                                promoInput = "SAVE10"
                                viewModel.setPromo("SAVE10")
                            },
                            label = { Text("SAVE10 (10% off)") }
                        )
                        androidx.compose.material3.AssistChip(
                            onClick = {
                                promoInput = "FREESHIP"
                                viewModel.setPromo("FREESHIP")
                            },
                            label = { Text("FREESHIP (Free Delivery)") }
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    SummaryRow("Items Subtotal (${summary.count})", formatPrice(summary.subtotal))
                    if (summary.discount > 0) {
                        SummaryRow("Promotional Savings", "- ${formatPrice(summary.discount)}")
                    }
                    SummaryRow("Delivery Charges", if (summary.shipping == 0.0) "FREE" else formatPrice(summary.shipping))
                    SummaryRow("Taxes (18% GST incl.)", formatPrice(summary.total * 0.18 / 1.18))

                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Order Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(formatPrice(summary.total), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                    }

                    if (summary.discount > 0 || summary.shipping == 0.0) {
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "Yay! You are saving ${formatPrice(summary.discount + if (summary.shipping == 0.0 && summary.subtotal > 0) 49.0 else 0.0)} on this order 🎉",
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = {
                    if (useCustomAddress) {
                        val phoneValid = phone.length == 10 && phone.all { it.isDigit() }
                        if (name.isBlank() || !phoneValid || address.isBlank() || city.isBlank() || pincode.length != 6) {
                            error = when {
                                name.isBlank() -> "Please enter your name"
                                !phoneValid -> "Please enter a valid 10-digit phone number"
                                address.isBlank() -> "Please enter delivery address"
                                city.isBlank() -> "Please enter city"
                                pincode.length != 6 -> "Please enter 6-digit pincode"
                                else -> "Please complete the form"
                            }
                            return@Button
                        }
                    }
                    placing = true
                    scope.launch {
                        val result = viewModel.placeOrder(currentFinalAddress, payment)
                        placing = false
                        result.onSuccess { onOrderPlaced(it) }.onFailure { error = it.message }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !placing,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (placing) {
                    CircularProgressIndicator(Modifier.padding(4.dp), color = Color.White)
                } else {
                    Text("Place Order • ${formatPrice(summary.total)}")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSuccessScreen(orderId: String, onViewOrders: () -> Unit, onContinue: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(topBar = { TopAppBar(title = { Text("Order Confirmed") }) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.CheckCircle, null, tint = SuccessGreen, modifier = Modifier.padding(16.dp).height(84.dp))
            Text("Thank you! 🎉", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Order $orderId is confirmed and will be packed by sellers shortly.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(20.dp))
            Button(onClick = onViewOrders, modifier = Modifier.fillMaxWidth()) { Text("View My Orders") }
            Spacer(Modifier.height(8.dp))
            androidx.compose.material3.OutlinedButton(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Continue Shopping") }
        }
    }
}
