package com.inikasir.domain.usecase.transaction

import com.inikasir.data.local.entity.TransactionEntity
import com.inikasir.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetAllTransactionsUseCase(private val transactionRepository: TransactionRepository) {
    
    operator fun invoke(): Flow<List<TransactionEntity>> {
        return transactionRepository.getAllTransactions()
    }
}