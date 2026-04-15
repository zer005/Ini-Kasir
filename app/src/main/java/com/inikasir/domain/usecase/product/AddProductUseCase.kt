package com.inikasir.domain.usecase.product

import com.inikasir.data.repository.ProductRepository

class AddProductUseCase(private val productRepository: ProductRepository) {
    
    suspend operator fun invoke(
        name: String, 
        price: Double, 
        stock: Int = 0,
        parentId: Long? = null,
        variantName: String? = null
    ): Long {
        require(name.isNotBlank()) { "Nama produk tidak boleh kosong" }
        require(price > 0) { "Harga harus lebih dari 0" }
        require(stock >= 0) { "Stok tidak boleh negatif" }
        
        return productRepository.insertProduct(name, price, stock, parentId, variantName)
    }
}