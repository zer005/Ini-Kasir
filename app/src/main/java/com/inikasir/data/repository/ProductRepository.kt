package com.inikasir.data.repository

import com.inikasir.data.local.AppDatabase
import com.inikasir.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val database: AppDatabase) {
    
    suspend fun insertProduct(name: String, price: Double): Long {
        val product = ProductEntity(name = name, price = price)
        return database.productDao().insert(product)
    }
    
    suspend fun updateProduct(id: Long, name: String, price: Double) {
        val product = ProductEntity(id = id, name = name, price = price)
        database.productDao().update(product)
    }
    
    suspend fun deleteProduct(product: ProductEntity) {
        database.productDao().delete(product)
    }
    
    suspend fun getProductById(id: Long): ProductEntity? {
        return database.productDao().getProductById(id)
    }
    
    fun getAllProducts(): Flow<List<ProductEntity>> {
        return database.productDao().getAllProducts()
    }
}