package com.example.multivendor.ui.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.MarketplaceRepository
import com.example.multivendor.data.Product
import com.example.multivendor.data.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ShopUiState(
    val query: String = "",
    val selectedCategoryId: String? = null,
    val sort: SortOption = SortOption.POPULAR,
    val products: List<Product> = emptyList(),
    val featured: List<Product> = emptyList()
)

class ShopViewModel(
    private val repository: MarketplaceRepository = AppContainer.repository
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _category = MutableStateFlow<String?>(null)
    private val _sort = MutableStateFlow(SortOption.POPULAR)

    val query = _query.asStateFlow()
    val selectedCategory = _category.asStateFlow()
    val sort = _sort.asStateFlow()

    val categories = repository.categories
    val vendors = repository.vendors
    val wishlistIds = repository.wishlistIds
    val cartCount = repository.cartCount.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val uiState: StateFlow<ShopUiState> = combine(
        repository.products, _query, _category, _sort
    ) { products, q, cat, s ->
        val filtered = repository.searchProducts(q, cat, s)
        ShopUiState(
            query = q,
            selectedCategoryId = cat,
            sort = s,
            products = filtered,
            featured = products.filter { it.isFeatured }.take(6)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ShopUiState())

    fun onQueryChange(value: String) { _query.value = value }
    fun onCategorySelect(id: String?) { _category.value = id }
    fun onSortChange(sort: SortOption) { _sort.value = sort }

    fun vendorName(vendorId: String): String =
        repository.getVendor(vendorId)?.name ?: "Unknown seller"

    fun addToCart(productId: String) {
        viewModelScope.launch { repository.addToCart(productId, 1) }
    }

    fun toggleWishlist(productId: String) {
        viewModelScope.launch { repository.toggleWishlist(productId) }
    }
}
