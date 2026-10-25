package com.example.multivendor.data

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class MarketplaceRepositoryTest {

    private lateinit var repository: MarketplaceRepository

    @Before
    fun setUp() {
        repository = DefaultDataRepository()
    }

    @Test
    fun catalog_loadsSampleData() = runTest {
        assertEquals(14, repository.products.first().size)
        assertEquals(11, repository.vendors.first().size)
        assertEquals(6, repository.categories.first().size)
    }

    @Test
    fun legacyData_exposesProductNames() = runTest {
        val names = (repository as DataRepository).data.first()
        assertTrue(names.isNotEmpty())
        assertTrue(names.contains("Aurora Wireless Headphones"))
    }

    @Test
    fun cart_addUpdateRemove_works() = runTest {
        assertEquals(0, repository.cartCount.first())

        repository.addToCart("p1", 2)
        assertEquals(2, repository.cartCount.first())
        assertEquals(1, repository.cartLines.first().size)

        repository.updateCartQuantity("p1", 5)
        assertEquals(5, repository.cartCount.first())

        repository.updateCartQuantity("p1", 0)
        assertEquals(0, repository.cartCount.first())

        repository.addToCart("p1", 1)
        repository.addToCart("p3", 1)
        assertEquals(2, repository.cartLines.first().size)
        repository.removeFromCart("p1")
        assertEquals(1, repository.cartLines.first().size)

        repository.clearCart()
        assertEquals(0, repository.cartCount.first())
    }

    @Test
    fun cart_respectsStockLimits() = runTest {
        // p11 has stock 25
        repository.addToCart("p11", 100)
        val line = repository.cartLines.first().first { it.product.id == "p11" }
        assertEquals(25, line.quantity)
    }

    @Test
    fun wishlist_toggle_works() = runTest {
        assertFalse(repository.wishlistIds.first().contains("p1"))
        repository.toggleWishlist("p1")
        assertTrue(repository.wishlistIds.first().contains("p1"))
        assertTrue(repository.isWishlisted("p1").first())
        repository.toggleWishlist("p1")
        assertFalse(repository.wishlistIds.first().contains("p1"))
    }

    @Test
    fun search_filtersByQueryAndCategory() {
        val byQuery = repository.searchProducts("headphones", null, SortOption.POPULAR)
        assertEquals(1, byQuery.size)
        assertEquals("p1", byQuery.first().id)

        val byCategory = repository.searchProducts("", "books", SortOption.POPULAR)
        assertTrue(byCategory.isNotEmpty())
        assertTrue(byCategory.all { it.categoryId == "books" })

        val all = repository.searchProducts("", null, SortOption.POPULAR)
        assertEquals(14, all.size)
    }

    @Test
    fun search_sortsCorrectly() {
        val lowHigh = repository.searchProducts("", null, SortOption.PRICE_LOW_HIGH)
        assertTrue(lowHigh.zipWithNext().all { (a, b) -> a.price <= b.price })

        val highLow = repository.searchProducts("", null, SortOption.PRICE_HIGH_LOW)
        assertTrue(highLow.zipWithNext().all { (a, b) -> a.price >= b.price })

        val rated = repository.searchProducts("", null, SortOption.RATING)
        assertTrue(rated.zipWithNext().all { (a, b) -> a.rating >= b.rating })
    }

    @Test
    fun promo_save10_appliesOnlyAbove999() {
        assertEquals(0.0, repository.discountForPromo("SAVE10", 500.0))
        assertEquals(150.0, repository.discountForPromo("SAVE10", 1500.0))
        assertEquals(150.0, repository.discountForPromo(" save10 ", 1500.0))
        assertEquals(0.0, repository.discountForPromo("INVALID", 1500.0))
        assertEquals(0.0, repository.discountForPromo(null, 1500.0))
    }

    @Test
    fun shipping_freeAbove499_orWithFreeship() {
        assertEquals(0.0, repository.shippingFor(600.0, null))
        assertEquals(49.0, repository.shippingFor(400.0, null))
        assertEquals(0.0, repository.shippingFor(100.0, "FREESHIP"))
        assertEquals(0.0, repository.shippingFor(0.0, null))
    }

    @Test
    fun placeOrder_createsOrderAndClearsCart() = runTest {
        repository.addToCart("p9", 2) // 499*2 = 998
        repository.addToCart("p11", 1) // +2799 = 3797
        val order = repository.placeOrder("Test Address, City - 123456", "UPI", "SAVE10")
        assertEquals(2, order.lines.size)
        assertTrue(order.total > 0)
        assertTrue(order.discount > 0)
        assertEquals("UPI", order.paymentMethod)
        assertEquals(1, repository.orders.first().size)
        assertEquals(0, repository.cartCount.first())
    }

    @Test(expected = IllegalArgumentException::class)
    fun placeOrder_invalidPromo_throws() = runTest {
        repository.addToCart("p1", 1)
        repository.placeOrder("addr", "COD", "WRONGCODE")
    }

    @Test(expected = IllegalArgumentException::class)
    fun placeOrder_emptyCart_throws() = runTest {
        repository.placeOrder("addr", "COD", null)
    }

    @Test
    fun reorder_restoresCartLines() = runTest {
        repository.addToCart("p1", 1)
        repository.addToCart("p3", 2)
        val order = repository.placeOrder("addr 123", "Cash on Delivery", null)
        assertEquals(0, repository.cartCount.first())
        repository.reorder(order.id)
        assertEquals(3, repository.cartCount.first())
    }

    @Test
    fun vendorLookups_work() {
        assertEquals("TechHub Store", repository.getVendor("techhub")?.name)
        assertEquals(4, repository.productsByVendor("techhub").size)
        assertEquals("Aurora Wireless Headphones", repository.getProduct("p1")?.name)
    }
}
