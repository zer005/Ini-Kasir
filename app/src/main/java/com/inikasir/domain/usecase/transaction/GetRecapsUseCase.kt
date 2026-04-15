package com.inikasir.domain.usecase.transaction

import com.inikasir.data.local.entity.RecapEntity
import com.inikasir.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetRecapsUseCase(private val transactionRepository: TransactionRepository) {

    operator fun invoke(): Flow<List<RecapEntity>> {
        return transactionRepository.getAllRecaps()
    }
}
