package com.inikasir.data.local.dao

import androidx.room.*
import com.inikasir.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long
    
    @Update
    suspend fun update(transaction: TransactionEntity)
    
    @Query("SELECT * FROM transactions WHERE isRecapped = 0 ORDER BY date DESC")
    fun getUnrecappedTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE recapId = :recapId ORDER BY date DESC")
    fun getTransactionsByRecapId(recapId: Long): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactionsSync(): List<TransactionEntity>
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?
    
    @Query("SELECT SUM(total) FROM transactions WHERE isRecapped = 0")
    suspend fun getUnrecappedTotal(): Double
    
    @Query("SELECT COUNT(*) FROM transactions WHERE isRecapped = 0")
    suspend fun getUnrecappedCount(): Int
    
    @Query("UPDATE transactions SET isRecapped = 1, recapId = :recapId WHERE isRecapped = 0")
    suspend fun markAsRecapped(recapId: Long)
}