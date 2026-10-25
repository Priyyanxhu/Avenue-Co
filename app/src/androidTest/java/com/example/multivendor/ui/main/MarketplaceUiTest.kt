package com.example.multivendor.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.multivendor.data.SampleData
import com.example.multivendor.theme.MultiVendorTheme
import com.example.multivendor.ui.components.EmptyState
import com.example.multivendor.ui.components.ProductCard
import com.example.multivendor.ui.shop.ShopScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import org.junit.Rule
import org.junit.Test

class MarketplaceUiTest {

    @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun shopScreen_rendersHeaderAndSearch() {
        composeTestRule.setContent {
            MultiVendorTheme { ShopScreen(onProductClick = {}, onVendorClick = {}) }
        }
        composeTestRule.onNodeWithText("Discover top multi-vendor picks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search products, brands, vendors…").assertIsDisplayed()
        composeTestRule.onNodeWithText("Shop by category").assertIsDisplayed()
    }

    @Test
    fun shopScreen_searchFiltersProducts() {
        composeTestRule.setContent {
            MultiVendorTheme { ShopScreen(onProductClick = {}, onVendorClick = {}) }
        }
        // Type a distinctive query; only matching product should remain.
        composeTestRule.onNodeWithText("Search products, brands, vendors…").performTextInput("Headphones")
        composeTestRule.onNodeWithText("Aurora Wireless Headphones").assertIsDisplayed()
    }

    @Test
    fun productCard_rendersDetailsAndHandlesClicks() {
        val product = SampleData.products.first()
        var clicked = false
        var added = false
        composeTestRule.setContent {
            MultiVendorTheme {
                ProductCard(
                    product = product,
                    vendorName = "TechHub Store",
                    isWishlisted = false,
                    onClick = { clicked = true },
                    onAddToCart = { added = true },
                    onWishlistToggle = {}
                )
            }
        }
        composeTestRule.onNodeWithText(product.name).assertIsDisplayed()
        composeTestRule.onNodeWithText("Add to Cart").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add to Cart").performClick()
        assert(added)
    }

    @Test
    fun emptyState_rendersTitleAndAction() {
        var actioned = false
        composeTestRule.setContent {
            MultiVendorTheme {
                EmptyState(
                    title = "No products found",
                    subtitle = "Try again",
                    icon = Icons.Filled.ShoppingCart,
                    actionLabel = "Clear filters",
                    onAction = { actioned = true }
                )
            }
        }
        composeTestRule.onNodeWithText("No products found").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear filters").performClick()
        assert(actioned)
    }
}
