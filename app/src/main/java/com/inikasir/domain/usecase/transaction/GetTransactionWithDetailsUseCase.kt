package com.inikasir.domain.usecase.transaction

import com.inikasir.data.local.entity.TransactionDetailEntity
import com.inikasir.data.local.entity.TransactionEntity
import com.inikasir.data.repository.TransactionRepository

class GetTransactionWithDetailsUseCase(private val transactionRepository: TransactionRepository) {
    
    suspend operator fun invoke(transactionId: Long): Pair<TransactionEntity?, List<TransactionDetailEntity>> {
        return transactionRepository.getTransactionWithDetails(transactionId)
    }
}