package com.inikasir.domain.usecase.product

import com.inikasir.data.repository.ProductRepository

class UpdateProductUseCase(private val productRepository: ProductRepository) {
    
    suspend operator fun invoke(id: Long, name: String, price: Double) {
        require(name.isNotBlank()) { "Nama produk tidak boleh kosong" }
        require(price > 0) { "Harga harus lebih dari 0" }
        productRepository.updateProduct(id, name, price)
    }
}