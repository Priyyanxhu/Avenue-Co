package com.example.multivendor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/** The app's own local database — single source of truth for catalog, cart, orders and users. */
@Database(
    entities = [
        UserEntity::class,
        ProductEntity::class,
        VendorEntity::class,
        CategoryEntity::class,
        CartItemEntity::class,
        WishlistEntity::class,
        OrderEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun vendorDao(): VendorDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        const val NAME = "multivendor.db"

        @Volatile
        private var instance: MarketplaceDatabase? = null

        fun get(context: Context): MarketplaceDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MarketplaceDatabase::class.java,
                    NAME
                ).build().also { instance = it }
            }
    }
}
