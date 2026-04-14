package com.inikasir.domain.usecase.product

import com.inikasir.data.repository.ProductRepository

class AddProductUseCase(private val productRepository: ProductRepository) {
    
    suspend operator fun invoke(name: String, price: Double): Long {
        require(name.isNotBlank()) { "Nama produk tidak boleh kosong" }
        require(price > 0) { "Harga harus lebih dari 0" }
        return productRepository.insertProduct(name, price)
    }
}