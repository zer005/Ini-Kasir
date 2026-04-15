package com.inikasir.presentation.admin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.inikasir.data.local.AppDatabase
import com.inikasir.data.repository.ProductRepository
import com.inikasir.data.repository.TransactionRepository
import com.inikasir.domain.usecase.product.AddProductUseCase
import com.inikasir.domain.usecase.product.DeleteProductUseCase
import com.inikasir.domain.usecase.product.GetAllProductsUseCase
import com.inikasir.domain.usecase.product.UpdateProductUseCase
import com.inikasir.domain.usecase.transaction.GetAllTransactionsUseCase
import com.inikasir.domain.usecase.transaction.GetRecapsUseCase

class AdminViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            val database = AppDatabase.getInstance(context)
            val productRepository = ProductRepository(database)
            val transactionRepository = TransactionRepository(database)

            val getAllProductsUseCase = GetAllProductsUseCase(productRepository)
            val addProductUseCase = AddProductUseCase(productRepository)
            val updateProductUseCase = UpdateProductUseCase(productRepository)
            val deleteProductUseCase = DeleteProductUseCase(productRepository)
            val getAllTransactionsUseCase = GetAllTransactionsUseCase(transactionRepository)
            val getRecapsUseCase = GetRecapsUseCase(transactionRepository)

            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(
                getAllProductsUseCase,
                addProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
                getAllTransactionsUseCase,
                getRecapsUseCase,
                transactionRepository,
                productRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}