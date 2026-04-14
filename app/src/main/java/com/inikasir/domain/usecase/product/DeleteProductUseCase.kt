package com.inikasir.domain.usecase.product

import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.data.repository.ProductRepository

class DeleteProductUseCase(private val productRepository: ProductRepository) {
    
    suspend operator fun invoke(product: ProductEntity) {
        productRepository.deleteProduct(product)
    }
}