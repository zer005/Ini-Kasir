package com.inikasir.domain.usecase.product

import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetAllProductsUseCase(private val productRepository: ProductRepository) {
    
    operator fun invoke(): Flow<List<ProductEntity>> {
        return productRepository.getMainProducts() // Ganti ke getMainProducts()
    }
}