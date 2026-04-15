package com.inikasir.data.local.dao

import androidx.room.*
import com.inikasir.data.local.entity.RecapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecapDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recap: RecapEntity): Long
    
    @Query("SELECT * FROM recaps ORDER BY createdAt DESC")
    fun getAllRecaps(): Flow<List<RecapEntity>>
    
    @Query("SELECT * FROM recaps WHERE id = :id")
    suspend fun getRecapById(id: Long): RecapEntity?
}