package com.inikasir.data.repository

import com.inikasir.data.local.AppDatabase
import com.inikasir.data.local.dao.TransactionDetailWithProduct
import com.inikasir.data.local.entity.RecapEntity
import com.inikasir.data.local.entity.TransactionDetailEntity
import com.inikasir.data.local.entity.TransactionEntity
import com.inikasir.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val database: AppDatabase) {
    
    suspend fun createTransaction(cartItems: List<CartItem>, total: Double): Long {
        // Insert transaction
        val transaction = TransactionEntity(total = total)
        val transactionId = database.transactionDao().insert(transaction)
        
        // Insert transaction details
        val details = cartItems.map { item ->
            TransactionDetailEntity(
                transactionId = transactionId,
                productId = item.productId,
                quantity = item.quantity,
                price = item.price,
                subtotal = item.subtotal
            )
        }
        database.transactionDetailDao().insertAll(details)
        
        // Decrease stock
        cartItems.forEach { item ->
            database.productDao().decreaseStock(item.productId, item.quantity)
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