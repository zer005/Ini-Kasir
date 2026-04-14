package com.inikasir.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.inikasir.data.local.dao.ProductDao
import com.inikasir.data.local.dao.TransactionDao
import com.inikasir.data.local.dao.TransactionDetailDao
import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.data.local.entity.TransactionDetailEntity
import com.inikasir.data.local.entity.TransactionEntity

@Database(
    entities = [
        ProductEntity::class,
        TransactionEntity::class,
        TransactionDetailEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionDetailDao(): TransactionDetailDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inikasir_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
