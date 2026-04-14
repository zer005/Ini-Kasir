package com.inikasir.presentation.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.data.local.entity.TransactionEntity
import com.inikasir.domain.usecase.product.AddProductUseCase
import com.inikasir.domain.usecase.product.DeleteProductUseCase
import com.inikasir.domain.usecase.product.GetAllProductsUseCase
import com.inikasir.domain.usecase.product.UpdateProductUseCase
import com.inikasir.domain.usecase.transaction.GetAllTransactionsUseCase
import com.inikasir.presentation.common.BaseViewModel
import kotlinx.coroutines.flow.map

class AdminViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase
) : BaseViewModel() {
    
    // Products
    val products = getAllProductsUseCase()
        .map { entities ->
            entities.map { entity ->
                ProductEntity(
                    id = entity.id,
                    name = entity.name,
                    price = entity.price
                )
            }
        }
        .asLiveData()
    
    // Transactions
    val transactions = getAllTransactionsUseCase()
        .map { entities ->
            entities.map { entity ->
                TransactionEntity(
                    id = entity.id,
                    total = entity.total,
                    date = entity.date
                )
            }
        }
        .asLiveData()
    
    // UI State
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message
    
    fun addProduct(name: String, price: Double) {
        if (name.isBlank() || price <= 0) {
            _message.value = "Nama dan harga harus valid"
            return
        }
        
        launch {
            try {
                _isLoading.postValue(true)
                addProductUseCase(name, price)
                _message.postValue("Produk berhasil ditambahkan")
            } catch (e: Exception) {
                _message.postValue("Gagal menambah produk: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    fun updateProduct(id: Long, name: String, price: Double) {
        if (name.isBlank() || price <= 0) {
            _message.value = "Nama dan harga harus valid"
            return
        }
        
        launch {
            try {
                _isLoading.postValue(true)
                updateProductUseCase(id, name, price)
                _message.postValue("Produk berhasil diupdate")
            } catch (e: Exception) {
                _message.postValue("Gagal update produk: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    fun deleteProduct(product: ProductEntity) {
        launch {
            try {
                _isLoading.postValue(true)
                deleteProductUseCase(product)
                _message.postValue("Produk berhasil dihapus")
            } catch (e: Exception) {
                _message.postValue("Gagal hapus produk: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    fun clearMessage() {
        _message.value = ""
    }
}