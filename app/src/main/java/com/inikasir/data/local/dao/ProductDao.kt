package com.inikasir.data.local.dao

import androidx.room.*
import com.inikasir.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity): Long
    
    @Update
    suspend fun update(product: ProductEntity)
    
    @Delete
    suspend fun delete(product: ProductEntity)
    
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?
    
    @Query("SELECT * FROM products WHERE parentId IS NULL ORDER BY name ASC")
    fun getMainProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE parentId = :parentId ORDER BY variantName ASC")
    fun getVariants(parentId: Long): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAllProductsSync(): List<ProductEntity>
    
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
    
    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId AND stock >= :quantity")
    suspend fun decreaseStock(productId: Long, quantity: Int): Int
    
    @Query("UPDATE products SET stock = stock + :quantity WHERE id = :productId")
    suspend fun increaseStock(productId: Long, quantity: Int)
}