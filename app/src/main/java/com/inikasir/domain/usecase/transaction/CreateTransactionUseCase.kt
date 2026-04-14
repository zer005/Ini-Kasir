package com.inikasir.domain.usecase.transaction

import com.inikasir.data.repository.TransactionRepository
import com.inikasir.domain.model.CartItem

class CreateTransactionUseCase(private val transactionRepository: TransactionRepository) {
    
    suspend operator fun invoke(cartItems: List<CartItem>, total: Double): Long {
        require(cartItems.isNotEmpty()) { "Keranjang tidak boleh kosong" }
        require(total > 0) { "Total harus lebih dari 0" }
        return transactionRepository.createTransaction(cartItems, total)
    }
}