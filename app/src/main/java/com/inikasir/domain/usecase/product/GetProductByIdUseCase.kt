package com.inikasir.domain.usecase.product

import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.data.repository.ProductRepository

class GetProductByIdUseCase(private val productRepository: ProductRepository) {
    
    suspend operator fun invoke(id: Long): ProductEntity? {
        return productRepository.getProductById(id)
    }
}