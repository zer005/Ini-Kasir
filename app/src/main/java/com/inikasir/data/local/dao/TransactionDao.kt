package com.inikasir.data.local.dao

import androidx.room.*
import com.inikasir.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactionsSync(): List<TransactionEntity>
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?
}