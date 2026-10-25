package com.example.multivendor.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.multivendor.ProductDetail
import com.example.multivendor.data.AppContainer
import com.example.multivendor.theme.MultiVendorTheme

/**
 * Legacy entry kept for backward compatibility.
 * Now renders loading/error/success states properly and forwards clicks
 * to [onItemClick] (previously ignored — dead navigation bug).
 */
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(AppContainer.repository) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (val s = state) {
        MainScreenUiState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Loading marketplace…", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        is MainScreenUiState.Success -> {
            MainScreen(
                data = s.data,
                modifier = modifier,
                onItemClick = { name -> onItemClick(ProductDetail(name)) }
            )
        }
        is MainScreenUiState.Error -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Something went wrong", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Error loading data: ${s.throwable.message}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
internal fun MainScreen(
    data: List<String>,
    modifier: Modifier = Modifier,
    onItemClick: ((String) -> Unit)? = null
) {
    if (data.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No items yet. Pull to refresh.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Welcome to MultiVendor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("${data.size} items from our sellers", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        items(data, key = { it }) { name ->
            ElevatedCard(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().let { m ->
                    if (onItemClick != null) m.clickable { onItemClick(name) } else m
                }
            ) {
                Column(Modifier.padding(16.dp)) {
                    // Greeting text preserved for existing UI tests.
                    Greeting(name)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Tap to view details →",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier, style = MaterialTheme.typography.titleMedium)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MultiVendorTheme { MainScreen(listOf("Android")) }
}

@Preview(showBackground = true, widthDp = 340)
@Composable
fun MainScreenPortraitPreview() {
    MultiVendorTheme { MainScreen(listOf("Android")) }
}
