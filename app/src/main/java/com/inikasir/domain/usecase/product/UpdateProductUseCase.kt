package com.inikasir.domain.usecase.product

import com.inikasir.data.repository.ProductRepository

class UpdateProductUseCase(private val productRepository: ProductRepository) {
    
    suspend operator fun invoke(id: Long, name: String, price: Double, stock: Int) {
        require(name.isNotBlank()) { "Nama produk tidak boleh kosong" }
        require(price > 0) { "Harga harus lebih dari 0" }
        require(stock >= 0) { "Stok tidak boleh negatif" }
        
        val existingProduct = productRepository.getProductById(id)
            ?: throw IllegalArgumentException("Produk tidak ditemukan")
        
        val updatedProduct = existingProduct.copy(
            name = name,
            price = price,
            stock = stock
        )
        
        productRepository.updateProduct(updatedProduct)
    }
}