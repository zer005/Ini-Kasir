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
}