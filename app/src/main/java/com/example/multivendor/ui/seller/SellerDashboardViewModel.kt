package com.example.multivendor.ui.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.MarketplaceRepository
import com.example.multivendor.data.Order
import com.example.multivendor.data.Product
import com.example.multivendor.data.Vendor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi

enum class StockFilter(val label: String) {
    ALL("All Products"),
    LOW_STOCK("Low Stock (≤10)"),
    OUT_OF_STOCK("Out of Stock (0)")
}

@OptIn(ExperimentalCoroutinesApi::class)
class SellerDashboardViewModel(
    initialVendorId: String? = null,
    private val repository: MarketplaceRepository = AppContainer.repository
) : ViewModel() {

    val vendors: StateFlow<List<Vendor>> = repository.vendors

    private val _selectedVendorId = MutableStateFlow(initialVendorId ?: vendors.value.firstOrNull()?.id ?: "techhub")
    val selectedVendorId: StateFlow<String> = _selectedVendorId.asStateFlow()

    val selectedVendor: StateFlow<Vendor?> = combine(vendors, _selectedVendorId) { list, id ->
        list.firstOrNull { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), vendors.value.firstOrNull())

    val vendorProducts: StateFlow<List<Product>> = combine(repository.products, _selectedVendorId) { all, vid ->
        all.filter { it.vendorId == vid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vendorOrders: StateFlow<List<Order>> = _selectedVendorId.flatMapLatest { vid ->
        if (vid.isEmpty()) flowOf(emptyList()) else repository.ordersForVendor(vid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vendorEarnings: StateFlow<Double> = _selectedVendorId.flatMapLatest { vid ->
        if (vid.isEmpty()) flowOf(0.0) else repository.vendorEarnings(vid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val searchQuery = MutableStateFlow("")
    val stockFilter = MutableStateFlow(StockFilter.ALL)

    val displayedProducts: StateFlow<List<Product>> = combine(
        vendorProducts,
        searchQuery,
        stockFilter
    ) { products, query, filter ->
        val q = query.trim().lowercase()
        products.filter { p ->
            val matchesQuery = q.isEmpty() ||
                p.name.lowercase().contains(q) ||
                p.description.lowercase().contains(q) ||
                p.tags.any { it.lowercase().contains(q) }
            val matchesStock = when (filter) {
                StockFilter.ALL -> true
                StockFilter.LOW_STOCK -> p.stock in 1..10
                StockFilter.OUT_OF_STOCK -> p.stock == 0
            }
            matchesQuery && matchesStock
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectVendor(vendorId: String) {
        _selectedVendorId.value = vendorId
    }

    fun quickAdjustStock(productId: String, delta: Int) {
        val product = repository.getProduct(productId) ?: return
        val newStock = (product.stock + delta).coerceAtLeast(0)
        viewModelScope.launch {
            repository.updateStock(productId, newStock)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
        }
    }

    fun registerNewStore(name: String, description: String, location: String): String {
        val id = "vendor-" + UUID.randomUUID().toString().take(6).lowercase()
        val newVendor = Vendor(
            id = id,
            name = name.trim(),
            description = description.trim(),
            rating = 5.0,
            location = location.trim(),
            isVerified = true,
            joinedYear = 2026,
            responseTime = "~1 hr",
            totalSales = 0
        )
        viewModelScope.launch {
            repository.registerVendor(newVendor)
            _selectedVendorId.value = id
        }
        return id
    }
}
