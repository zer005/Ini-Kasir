package com.inikasir.data.local.dao

import androidx.room.*
import com.inikasir.data.local.entity.TransactionDetailEntity

@Dao
interface TransactionDetailDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(detail: TransactionDetailEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(details: List<TransactionDetailEntity>)
    
    @Query("SELECT * FROM transaction_details WHERE transactionId = :transactionId")
    suspend fun getDetailsByTransactionId(transactionId: Long): List<TransactionDetailEntity>
    
    @Query("SELECT * FROM transaction_details")
    suspend fun getAllDetailsSync(): List<TransactionDetailEntity>
    
    @Query("""
        SELECT td.*, p.name as productName, p.variantName 
        FROM transaction_details td 
        INNER JOIN products p ON td.productId = p.id 
        WHERE td.transactionId = :transactionId
    """)
    suspend fun getDetailsWithProduct(transactionId: Long): List<TransactionDetailWithProduct>
}

data class TransactionDetailWithProduct(
    val id: Long,
    val transactionId: Long,
    val productId: Long,
    val quantity: Int,
    val price: Double,
    val subtotal: Double,
    val productName: String,
    val variantName: String?
)