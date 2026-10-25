package com.example.multivendor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<UserEntity?>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAllOnce(): List<ProductEntity>

    @Query("SELECT * FROM products")
    fun observeAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE vendorId = :vendorId")
    suspend fun getByVendor(vendorId: String): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int
}

@Dao
interface VendorDao {
    @Query("SELECT * FROM vendors")
    suspend fun getAllOnce(): List<VendorEntity>

    @Query("SELECT * FROM vendors")
    fun observeAll(): Flow<List<VendorEntity>>

    @Query("SELECT * FROM vendors WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): VendorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(vendors: List<VendorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vendor: VendorEntity)

    @Query("SELECT COUNT(*) FROM vendors")
    suspend fun count(): Int
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllOnce(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(categories: List<CategoryEntity>)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    suspend fun getAllOnce(): List<CartItemEntity>

    @Query("SELECT * FROM cart_items")
    fun observeAll(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteById(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clear()
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist")
    suspend fun getAllOnce(): List<WishlistEntity>

    @Query("SELECT * FROM wishlist")
    fun observeAll(): Flow<List<WishlistEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: WishlistEntity): Long

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun deleteById(productId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :productId)")
    suspend fun exists(productId: String): Boolean

    @Query("DELETE FROM wishlist")
    suspend fun clear()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    suspend fun getAllOnce(): List<OrderEntity>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateStatus(orderId: String, status: String)

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getById(orderId: String): OrderEntity?
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews")
    suspend fun getAllOnce(): List<ReviewEntity>

    @Query("SELECT * FROM reviews")
    fun observeAll(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(reviews: List<ReviewEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(review: ReviewEntity)

    @Query("UPDATE reviews SET helpfulCount = helpfulCount + 1 WHERE id = :reviewId")
    suspend fun incrementHelpful(reviewId: String)

    @Query("SELECT COUNT(*) FROM reviews")
    suspend fun count(): Int
}
