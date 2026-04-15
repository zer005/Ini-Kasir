package com.inikasir.data.repository

import androidx.room.withTransaction
import com.inikasir.data.local.AppDatabase
import com.inikasir.data.local.dao.TransactionDetailWithProduct
import com.inikasir.data.local.entity.RecapEntity
import com.inikasir.data.local.entity.TransactionDetailEntity
import com.inikasir.data.local.entity.TransactionEntity
import com.inikasir.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val database: AppDatabase) {
    
    suspend fun createTransaction(cartItems: List<CartItem>, total: Double): Long {
        val transaction = TransactionEntity(total = total)
        
        // Validate stock before transaction
        cartItems.forEach { item ->
            val product = database.productDao().getProductById(item.productId)
            if (product == null || product.stock < item.quantity) {
                throw Exception("Stok tidak cukup untuk: ${item.productName}")
            }
        }

        // Insert transaction first to get the ID
        val transactionId = database.transactionDao().insert(transaction)

        // Create details with the transaction ID
        val details = cartItems.map { item ->
            TransactionDetailEntity(
                transactionId = transactionId,
                productId = item.productId,
                quantity = item.quantity,
                price = item.price,
                subtotal = item.subtotal
            )
        }

        // Execute details insert and stock update in a transaction
        database.withTransaction {
            // Insert all details
            database.transactionDetailDao().insertAll(details)

            // Decrease stock for each item
            cartItems.forEach { item ->
                database.productDao().decreaseStock(item.productId, item.quantity)
            }
        }

        return transactionId
    }
    
    fun getUnrecappedTransactions(): Flow<List<TransactionEntity>> {
        return database.transactionDao().getUnrecappedTransactions()
    }
    
    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return database.transactionDao().getAllTransactions()
    }
    
    fun getTransactionsByRecapId(recapId: Long): Flow<List<TransactionEntity>> {
        return database.transactionDao().getTransactionsByRecapId(recapId)
    }
    
    suspend fun getTransactionWithDetails(transactionId: Long): Pair<TransactionEntity?, List<TransactionDetailWithProduct>> {
        val transaction = database.transactionDao().getTransactionById(transactionId)
        val details = database.transactionDetailDao().getDetailsWithProduct(transactionId)
        return Pair(transaction, details)
    }
    
    suspend fun createRecap(): Long {
        val totalRevenue = database.transactionDao().getUnrecappedTotal()
        val transactionCount = database.transactionDao().getUnrecappedCount()
        
        if (transactionCount == 0) return 0
        
        val unrecappedTransactions = database.transactionDao().getAllTransactionsSync()
            .filter { !it.isRecapped }
        
        val startDate = unrecappedTransactions.minOfOrNull { it.date } ?: System.currentTimeMillis()
        val endDate = unrecappedTransactions.maxOfOrNull { it.date } ?: System.currentTimeMillis()
        
        val recap = RecapEntity(
            startDate = startDate,
            endDate = endDate,
            totalRevenue = totalRevenue,
            transactionCount = transactionCount
        )
        
        val recapId = database.recapDao().insert(recap)
        database.transactionDao().markAsRecapped(recapId)
        
        return recapId
    }
    
    fun getAllRecaps(): Flow<List<RecapEntity>> {
        return database.recapDao().getAllRecaps()
    }
}