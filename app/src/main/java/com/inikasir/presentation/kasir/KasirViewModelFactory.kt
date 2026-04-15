package com.inikasir.presentation.kasir

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inikasir.data.local.AppDatabase
import com.inikasir.data.repository.ProductRepository
import com.inikasir.data.repository.TransactionRepository
import com.inikasir.domain.usecase.product.GetAllProductsUseCase
import com.inikasir.domain.usecase.transaction.CreateTransactionUseCase

class KasirViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KasirViewModel::class.java)) {
            val database = AppDatabase.getInstance(context)
            val productRepository = ProductRepository(database)
            val transactionRepository = TransactionRepository(database)
            
            val getAllProductsUseCase = GetAllProductsUseCase(productRepository)
            val createTransactionUseCase = CreateTransactionUseCase(transactionRepository)
            
            @Suppress("UNCHECKED_CAST")
            return KasirViewModel(
                getAllProductsUseCase = getAllProductsUseCase,
                createTransactionUseCase = createTransactionUseCase,
                productRepository = productRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}