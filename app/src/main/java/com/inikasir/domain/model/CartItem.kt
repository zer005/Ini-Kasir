package com.inikasir.domain.model

data class CartItem(
    val productId: Long,
    val productName: String,
    val price: Double,
    var quantity: Int,
    val subtotal: Double
) {
    fun incrementQuantity() {
        // Will be handled in ViewModel
    }
    
    fun decrementQuantity() {
        // Will be handled in ViewModel
    }
}