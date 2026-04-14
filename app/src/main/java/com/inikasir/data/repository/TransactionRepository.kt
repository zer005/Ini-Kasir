package com.inikasir.data.repository

import com.inikasir.data.local.AppDatabase
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
                subtotal = item.subtotal
            )
        }
        database.transactionDetailDao().insertAll(details)
        
        return transactionId
    }
    
    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return database.transactionDao().getAllTransactions()
    }
    
    suspend fun getTransactionWithDetails(transactionId: Long): Pair<TransactionEntity?, List<TransactionDetailEntity>> {
        val transaction = database.transactionDao().getTransactionById(transactionId)
        val details = database.transactionDetailDao().getDetailsByTransactionId(transactionId)
        return Pair(transaction, details)
    }
}