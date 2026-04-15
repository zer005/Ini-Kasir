package com.inikasir.data.repository

import com.inikasir.data.local.AppDatabase
import com.inikasir.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val database: AppDatabase) {
    
    suspend fun insertProduct(
        name: String, 
        price: Double, 
        stock: Int = 0,
        parentId: Long? = null,
        variantName: String? = null
    ): Long {
        val product = ProductEntity(
            name = name,
            price = price,
            stock = stock,
            parentId = parentId,
            variantName = variantName
        )
        return database.productDao().insert(product)
    }
    
    suspend fun updateProduct(product: ProductEntity) {
        database.productDao().update(product)
    }
    
    suspend fun deleteProduct(product: ProductEntity) {
        database.productDao().delete(product)
    }
    
    suspend fun getProductById(id: Long): ProductEntity? {
        return database.productDao().getProductById(id)
    }
    
    fun getMainProducts(): Flow<List<ProductEntity>> {
        return database.productDao().getMainProducts()
    }
    
    fun getVariants(parentId: Long): Flow<List<ProductEntity>> {
        return database.productDao().getVariants(parentId)
    }
    
    suspend fun decreaseStock(productId: Long, quantity: Int): Boolean {
        return database.productDao().decreaseStock(productId, quantity) > 0
    }
    
    suspend fun increaseStock(productId: Long, quantity: Int) {
        database.productDao().increaseStock(productId, quantity)
    }
}