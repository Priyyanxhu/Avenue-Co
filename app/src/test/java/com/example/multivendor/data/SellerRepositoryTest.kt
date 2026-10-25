package com.example.multivendor.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SellerRepositoryTest {

    private lateinit var repository: MarketplaceRepository

    @Before
    fun setUp() {
        repository = AppContainer.freshRepository()
    }

    @Test
    fun addProduct_insertsAtBeginningAndIsRetrievable() = runTest {
        val newProduct = Product(
            id = "test-prod-1",
            name = "Test Product Pro",
            description = "Test Description",
            price = 1999.0,
            originalPrice = 2999.0,
            categoryId = "electronics",
            vendorId = "techhub",
            stock = 50
        )
        repository.addProduct(newProduct)

        val retrieved = repository.getProduct("test-prod-1")
        assertNotNull(retrieved)
        assertEquals("Test Product Pro", retrieved?.name)
        assertEquals(50, retrieved?.stock)

        val vendorProducts = repository.productsByVendor("techhub")
        assertTrue(vendorProducts.any { it.id == "test-prod-1" })
    }

    @Test
    fun updateProduct_modifiesFieldsCorrectly() = runTest {
        val existing = repository.getProduct("p1")
        assertNotNull(existing)

        val updated = existing!!.copy(price = 1999.0, stock = 100, name = "Aurora Wireless Headphones V2")
        repository.updateProduct(updated)

        val retrieved = repository.getProduct("p1")
        assertEquals(1999.0, retrieved?.price ?: 0.0, 0.01)
        assertEquals(100, retrieved?.stock)
        assertEquals("Aurora Wireless Headphones V2", retrieved?.name)
    }

    @Test
    fun updateStock_clampsToZero() = runTest {
        repository.updateStock("p1", 15)
        assertEquals(15, repository.getProduct("p1")?.stock)

        repository.updateStock("p1", -5)
        assertEquals(0, repository.getProduct("p1")?.stock)
    }

    @Test
    fun deleteProduct_removesFromCatalogCartAndWishlist() = runTest {
        repository.addToCart("p1", 1)
        repository.toggleWishlist("p1")
        assertTrue(repository.isWishlisted("p1").first())

        repository.deleteProduct("p1")

        assertNull(repository.getProduct("p1"))
        assertFalse(repository.isWishlisted("p1").first())
        assertEquals(0, repository.cartCount.first())
    }

    @Test
    fun registerVendor_addsNewStore() = runTest {
        val newVendor = Vendor(
            id = "test-store",
            name = "Super Electronics",
            description = "Top gadgets",
            rating = 5.0,
            location = "Delhi, India"
        )
        repository.registerVendor(newVendor)

        val retrieved = repository.getVendor("test-store")
        assertNotNull(retrieved)
        assertEquals("Super Electronics", retrieved?.name)
    }

    @Test
    fun vendorOrdersAndEarnings_filtersByVendorIdCorrectly() = runTest {
        val techhubOrders = repository.ordersForVendor("techhub").first()
        val techhubEarnings = repository.vendorEarnings("techhub").first()

        // Since freshRepository starts with empty orders, place an order with techhub products
        repository.addToCart("p1", 2) // 2999 * 2 = 5998 (techhub)
        repository.addToCart("p9", 1) // 499 (bookworm)
        val order = repository.placeOrder("Test Indiranagar Address", "UPI", null)

        val updatedOrders = repository.ordersForVendor("techhub").first()
        assertEquals(1, updatedOrders.size)
        assertEquals(order.id, updatedOrders.first().id)

        val updatedEarnings = repository.vendorEarnings("techhub").first()
        assertEquals(5998.0, updatedEarnings, 0.01)

        // Bookworm earnings should only be 499
        val bookwormEarnings = repository.vendorEarnings("bookworm").first()
        assertEquals(499.0, bookwormEarnings, 0.01)
    }

    @Test
    fun updateOrderStatus_updatesStatusAndExcludesCancelledFromEarnings() = runTest {
        repository.addToCart("p1", 1) // 2999
        val order = repository.placeOrder("Address", "COD", null)

        repository.updateOrderStatus(order.id, "Shipped")
        val shippedOrder = repository.orders.first().first { it.id == order.id }
        assertEquals("Shipped", shippedOrder.status)

        // If cancelled, earnings should be 0
        repository.updateOrderStatus(order.id, "Cancelled")
        val earnings = repository.vendorEarnings("techhub").first()
        assertEquals(0.0, earnings, 0.01)
    }

    @Test
    fun addReview_updatesProductRatingAndRatingBreakdown() = runTest {
        val review = Review(
            id = "test-rev-1",
            productId = "p1",
            userName = "Rohit Sharma",
            rating = 5,
            title = "Superb Quality!",
            comment = "Exceeded all expectations",
            date = "02 Oct 2026",
            isVerifiedBuyer = true,
            helpfulCount = 0
        )
        repository.addReview(review)

        val reviews = repository.reviewsForProduct("p1").first()
        assertEquals(1, reviews.size)
        assertEquals("Rohit Sharma", reviews.first().userName)

        val breakdown = repository.ratingBreakdownFor("p1").first()
        assertEquals(1, breakdown.totalReviews)
        assertEquals(5.0, breakdown.averageRating, 0.01)
        assertEquals(1, breakdown.starCounts[5])

        // Upvote helpful
        repository.markReviewHelpful("test-rev-1")
        val updatedReviews = repository.reviewsForProduct("p1").first()
        assertEquals(1, updatedReviews.first().helpfulCount)
    }
}
