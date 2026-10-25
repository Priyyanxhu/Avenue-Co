package com.example.multivendor.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Legacy interface kept for backward compatibility with existing ViewModel/tests.
interface DataRepository {
    val data: Flow<List<String>>
}

interface MarketplaceRepository : DataRepository {
    val products: StateFlow<List<Product>>
    val vendors: StateFlow<List<Vendor>>
    val categories: StateFlow<List<Category>>

    val cartLines: Flow<List<CartLine>>
    val cartCount: Flow<Int>
    val cartSubtotal: Flow<Double>
    val wishlistIds: StateFlow<Set<String>>
    val orders: StateFlow<List<Order>>

    suspend fun addToCart(productId: String, quantity: Int = 1)
    suspend fun updateCartQuantity(productId: String, quantity: Int)
    suspend fun removeFromCart(productId: String)
    suspend fun clearCart()

    suspend fun toggleWishlist(productId: String)
    fun isWishlisted(productId: String): Flow<Boolean>

    suspend fun placeOrder(address: String, paymentMethod: String, promoCode: String?): Order
    suspend fun reorder(orderId: String)

    fun getProduct(productId: String): Product?
    fun getVendor(vendorId: String): Vendor?
    fun productsByVendor(vendorId: String): List<Product>
    fun searchProducts(query: String, categoryId: String?, sort: SortOption): List<Product>

    fun discountForPromo(promoCode: String?, subtotal: Double): Double
    fun shippingFor(subtotal: Double, promoCode: String?): Double

    suspend fun addProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(productId: String)
    suspend fun updateStock(productId: String, newStock: Int)
    suspend fun registerVendor(vendor: Vendor)
    suspend fun updateVendor(vendor: Vendor)
    suspend fun updateOrderStatus(orderId: String, newStatus: String)

    fun ordersForVendor(vendorId: String): Flow<List<Order>>
    fun vendorEarnings(vendorId: String): Flow<Double>

    val reviews: StateFlow<List<Review>>
    fun reviewsForProduct(productId: String): Flow<List<Review>>
    fun ratingBreakdownFor(productId: String): Flow<RatingBreakdown>
    suspend fun addReview(review: Review)
    suspend fun markReviewHelpful(reviewId: String)
}

class DefaultDataRepository(
    initialOrders: List<Order> = emptyList(),
    initialReviews: List<Review> = emptyList()
) : MarketplaceRepository {
    private val _products = MutableStateFlow(SampleData.products)
    private val _vendors = MutableStateFlow(SampleData.vendors)
    private val _categories = MutableStateFlow(SampleData.categories)

    private val _cart = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
    private val _orders = MutableStateFlow(initialOrders)
    private val _reviews = MutableStateFlow(initialReviews)

    override val products: StateFlow<List<Product>> = _products.asStateFlow()
    override val vendors: StateFlow<List<Vendor>> = _vendors.asStateFlow()
    override val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    override val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()
    override val wishlistIds: StateFlow<Set<String>> = _wishlist.asStateFlow()
    override val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // Legacy: expose product names so old MainScreenViewModel keeps working.
    override val data: Flow<List<String>> = _products.map { list -> list.map { it.name } }

    override val cartLines: Flow<List<CartLine>> = combine(_cart, _products) { cart, products ->
        val byId = products.associateBy { it.id }
        cart.mapNotNull { (id, qty) -> byId[id]?.let { CartLine(it, qty) } }
    }

    override val cartCount: Flow<Int> = _cart.map { it.values.sum() }
    override val cartSubtotal: Flow<Double> = cartLines.map { lines -> lines.sumOf { it.lineTotal } }

    override suspend fun addToCart(productId: String, quantity: Int) {
        val product = getProduct(productId) ?: return
        val current = _cart.value.toMutableMap()
        val existing = current[productId] ?: 0
        val newQty = (existing + quantity).coerceIn(1, product.stock.coerceAtLeast(1))
        current[productId] = newQty
        _cart.value = current
    }

    override suspend fun updateCartQuantity(productId: String, quantity: Int) {
        val product = getProduct(productId) ?: return
        val current = _cart.value.toMutableMap()
        if (quantity <= 0) {
            current.remove(productId)
        } else {
            current[productId] = quantity.coerceIn(1, product.stock.coerceAtLeast(1))
        }
        _cart.value = current
    }

    override suspend fun removeFromCart(productId: String) {
        val current = _cart.value.toMutableMap()
        current.remove(productId)
        _cart.value = current
    }

    override suspend fun clearCart() {
        _cart.value = emptyMap()
    }

    override suspend fun toggleWishlist(productId: String) {
        val current = _wishlist.value.toMutableSet()
        if (!current.add(productId)) current.remove(productId)
        _wishlist.value = current
    }

    override fun isWishlisted(productId: String): Flow<Boolean> =
        _wishlist.map { it.contains(productId) }

    override fun discountForPromo(promoCode: String?, subtotal: Double): Double {
        val code = promoCode?.trim()?.uppercase().orEmpty()
        if (code.isEmpty()) return 0.0
        return when (code) {
            "SAVE10" -> if (subtotal >= 999) subtotal * 0.10 else 0.0
            else -> 0.0
        }
    }

    override fun shippingFor(subtotal: Double, promoCode: String?): Double {
        if (subtotal <= 0.0) return 0.0
        val code = promoCode?.trim()?.uppercase().orEmpty()
        if (code == "FREESHIP") return 0.0
        return if (subtotal >= 499) 0.0 else 49.0
    }

    override suspend fun placeOrder(address: String, paymentMethod: String, promoCode: String?): Order {
        val lines = cartLinesSnapshot()
        require(lines.isNotEmpty()) { "Cart is empty" }
        require(address.isNotBlank()) { "Address is required" }
        val subtotal = lines.sumOf { it.lineTotal }
        val normalizedPromo = promoCode?.trim()?.uppercase()?.takeIf { it.isNotEmpty() }
        if (normalizedPromo != null && normalizedPromo != "SAVE10" && normalizedPromo != "FREESHIP") {
            throw IllegalArgumentException("Invalid promo code")
        }
        val discount = discountForPromo(normalizedPromo, subtotal)
        val shipping = shippingFor(subtotal - discount, normalizedPromo)
        val total = (subtotal - discount + shipping).coerceAtLeast(0.0)
        val order = Order(
            id = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}",
            lines = lines.map { OrderLine(it.product.id, it.product.name, it.product.price, it.quantity, it.product.vendorId) },
            subtotal = subtotal,
            discount = discount,
            shipping = shipping,
            total = total,
            status = "Confirmed",
            date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
            address = address,
            paymentMethod = paymentMethod,
            promoCode = normalizedPromo
        )
        // Deduct inventory
        val qtyById = lines.associate { it.product.id to it.quantity }
        _products.value = _products.value.map { p ->
            val q = qtyById[p.id]
            if (q != null) p.copy(stock = (p.stock - q).coerceAtLeast(0)) else p
        }
        _orders.value = listOf(order) + _orders.value
        _cart.value = emptyMap()
        return order
    }

    override suspend fun reorder(orderId: String) {
        val order = _orders.value.firstOrNull { it.id == orderId } ?: return
        val current = _cart.value.toMutableMap()
        order.lines.forEach { line ->
            val product = getProduct(line.productId) ?: return@forEach
            val existing = current[line.productId] ?: 0
            current[line.productId] = (existing + line.quantity).coerceIn(1, product.stock.coerceAtLeast(1))
        }
        _cart.value = current
    }

    override fun getProduct(productId: String): Product? = _products.value.firstOrNull { it.id == productId }

    override fun getVendor(vendorId: String): Vendor? = _vendors.value.firstOrNull { it.id == vendorId }

    override fun productsByVendor(vendorId: String): List<Product> =
        _products.value.filter { it.vendorId == vendorId }

    override fun searchProducts(query: String, categoryId: String?, sort: SortOption): List<Product> {
        val q = query.trim().lowercase()
        val vendorsById = _vendors.value.associateBy { it.id }
        var result = _products.value.filter { p ->
            val matchesQuery = q.isEmpty() ||
                p.name.lowercase().contains(q) ||
                p.description.lowercase().contains(q) ||
                p.tags.any { it.lowercase().contains(q) } ||
                (vendorsById[p.vendorId]?.name?.lowercase()?.contains(q) == true)
            matchesQuery && (categoryId == null || p.categoryId == categoryId)
        }
        result = when (sort) {
            SortOption.PRICE_LOW_HIGH -> result.sortedBy { it.price }
            SortOption.PRICE_HIGH_LOW -> result.sortedByDescending { it.price }
            SortOption.RATING -> result.sortedByDescending { it.rating }
            SortOption.NEWEST -> result.sortedWith(compareByDescending<Product> { it.isNew }.thenByDescending { it.reviews })
            SortOption.POPULAR -> result.sortedByDescending { it.reviews }
        }
        return result
    }

    override suspend fun addProduct(product: Product) {
        _products.value = listOf(product) + _products.value
    }

    override suspend fun updateProduct(product: Product) {
        _products.value = _products.value.map { if (it.id == product.id) product else it }
    }

    override suspend fun deleteProduct(productId: String) {
        _products.value = _products.value.filterNot { it.id == productId }
        removeFromCart(productId)
        val currentWish = _wishlist.value.toMutableSet()
        if (currentWish.remove(productId)) {
            _wishlist.value = currentWish
        }
    }

    override suspend fun updateStock(productId: String, newStock: Int) {
        val clamped = newStock.coerceAtLeast(0)
        _products.value = _products.value.map {
            if (it.id == productId) it.copy(stock = clamped) else it
        }
    }

    override suspend fun registerVendor(vendor: Vendor) {
        _vendors.value = _vendors.value + vendor
    }

    override suspend fun updateVendor(vendor: Vendor) {
        _vendors.value = _vendors.value.map { if (it.id == vendor.id) vendor else it }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) it.copy(status = newStatus) else it
        }
    }

    override fun ordersForVendor(vendorId: String): Flow<List<Order>> = combine(_orders, _products) { orderList, products ->
        val productVendorById = products.associate { it.id to it.vendorId }
        orderList.filter { order ->
            order.lines.any { line ->
                line.vendorId == vendorId || productVendorById[line.productId] == vendorId
            }
        }
    }

    override fun vendorEarnings(vendorId: String): Flow<Double> = combine(ordersForVendor(vendorId), _products) { vendorOrders, products ->
        val productVendorById = products.associate { it.id to it.vendorId }
        vendorOrders.filter { it.status != "Cancelled" }.sumOf { order ->
            order.lines
                .filter { it.vendorId == vendorId || productVendorById[it.productId] == vendorId }
                .sumOf { it.unitPrice * it.quantity }
        }
    }

    override fun reviewsForProduct(productId: String): Flow<List<Review>> = _reviews.map { list ->
        list.filter { it.productId == productId }
    }

    override fun ratingBreakdownFor(productId: String): Flow<RatingBreakdown> = combine(_products, _reviews) { products, reviews ->
        val product = products.firstOrNull { it.id == productId }
        val prodReviews = reviews.filter { it.productId == productId }
        val counts = (1..5).associateWith { star -> prodReviews.count { it.rating == star } }
        val avg = if (prodReviews.isNotEmpty()) {
            prodReviews.map { it.rating }.average()
        } else {
            product?.rating ?: 4.0
        }
        val total = if (prodReviews.isNotEmpty()) prodReviews.size else (product?.reviews ?: 0)
        RatingBreakdown(averageRating = avg, totalReviews = total, starCounts = counts)
    }

    override suspend fun addReview(review: Review) {
        _reviews.value = listOf(review) + _reviews.value
        val currentProducts = _products.value.toMutableList()
        val idx = currentProducts.indexOfFirst { it.id == review.productId }
        if (idx != -1) {
            val p = currentProducts[idx]
            val allProdReviews = _reviews.value.filter { it.productId == review.productId }
            val newAvg = if (allProdReviews.isNotEmpty()) allProdReviews.map { it.rating }.average() else review.rating.toDouble()
            currentProducts[idx] = p.copy(
                rating = (Math.round(newAvg * 10.0) / 10.0),
                reviews = p.reviews + 1
            )
            _products.value = currentProducts
        }
    }

    override suspend fun markReviewHelpful(reviewId: String) {
        _reviews.value = _reviews.value.map {
            if (it.id == reviewId) it.copy(helpfulCount = it.helpfulCount + 1) else it
        }
    }

    private fun cartLinesSnapshot(): List<CartLine> {
        val byId = _products.value.associateBy { it.id }
        return _cart.value.mapNotNull { (id, qty) -> byId[id]?.let { CartLine(it, qty) } }
    }
}

/** Shared singleton so cart/wishlist/orders persist across ViewModels. */
object AppContainer {
    @Volatile
    private var initialized = false

    private var roomRepository: RoomMarketplaceRepository? = null
    private var _authRepository: com.example.multivendor.data.auth.AuthRepository? = null

    private val fallbackRepository: MarketplaceRepository by lazy {
        DefaultDataRepository(
            initialOrders = SampleData.initialOrders,
            initialReviews = SampleData.initialReviews
        )
    }

    /** Production marketplace repository: Room-backed after [init], in-memory fallback otherwise (tests/previews). */
    val repository: MarketplaceRepository
        get() = roomRepository ?: fallbackRepository

    /** Auth repository backed by the app's own database. Requires [init] first. */
    val authRepository: com.example.multivendor.data.auth.AuthRepository
        get() = _authRepository ?: error("AppContainer.init(context) must be called before using authRepository")

    /** True once [init] has wired the Room database. */
    fun isInitialized(): Boolean = initialized

    /**
     * Wire the app's own local database. Call once from the Activity/Application.
     * Returns the Room-backed repository and kicks off DB load/seed in the background.
     */
    fun init(context: android.content.Context): RoomMarketplaceRepository {
        roomRepository?.let { return it }
        synchronized(this) {
            roomRepository?.let { return it }
            val appContext = context.applicationContext
            val db = com.example.multivendor.data.local.MarketplaceDatabase.get(appContext)
            _authRepository = com.example.multivendor.data.auth.RoomAuthRepository(appContext, db)
            val repo = RoomMarketplaceRepository(db)
            roomRepository = repo
            initialized = true
            kotlinx.coroutines.MainScope().launch(kotlinx.coroutines.Dispatchers.IO) {
                runCatching { repo.loadFromDatabase() }
            }
            return repo
        }
    }

    /** For tests: create an isolated repository. */
    fun freshRepository(): MarketplaceRepository = DefaultDataRepository(
        initialOrders = emptyList(),
        initialReviews = emptyList()
    )
}
