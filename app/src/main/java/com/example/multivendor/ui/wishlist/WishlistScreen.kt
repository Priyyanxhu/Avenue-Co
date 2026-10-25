package com.example.multivendor.ui.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.MarketplaceRepository
import com.example.multivendor.ui.components.EmptyState
import com.example.multivendor.ui.components.ProductCard
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WishlistViewModel(
    private val repository: MarketplaceRepository = AppContainer.repository
) : ViewModel() {
    val uiState = combine(repository.products, repository.wishlistIds) { products, ids ->
        products.filter { ids.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistIds = repository.wishlistIds

    fun toggle(id: String) { viewModelScope.launch { repository.toggleWishlist(id) } }
    fun addToCart(id: String) { viewModelScope.launch { repository.addToCart(id, 1) } }
    fun vendorName(vendorId: String) = repository.getVendor(vendorId)?.name ?: ""
}

@Composable
fun WishlistScreen(
    onProductClick: (String) -> Unit,
    onBrowse: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WishlistViewModel = viewModel { WishlistViewModel(AppContainer.repository) }
) {
    val items by viewModel.uiState.collectAsState()
    val wishlist by viewModel.wishlistIds.collectAsState()

    if (items.isEmpty()) {
        EmptyState(
            title = "Wishlist is empty",
            subtitle = "Tap the heart on any product to save it here.",
            icon = Icons.Filled.FavoriteBorder,
            actionLabel = "Discover products",
            onAction = onBrowse,
            modifier = modifier
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text("Wishlist (${items.size}) ❤️", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(items, key = { it.id }) { product ->
            ProductCard(
                product = product,
                vendorName = viewModel.vendorName(product.vendorId),
                isWishlisted = wishlist.contains(product.id),
                onClick = { onProductClick(product.id) },
                onAddToCart = { viewModel.addToCart(product.id) },
                onWishlistToggle = { viewModel.toggle(product.id) }
            )
        }
    }
}
