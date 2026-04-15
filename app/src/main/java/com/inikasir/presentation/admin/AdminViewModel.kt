package com.inikasir.presentation.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.inikasir.data.local.dao.TransactionDetailWithProduct
import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.data.local.entity.RecapEntity
import com.inikasir.data.local.entity.TransactionEntity
import com.inikasir.data.repository.ProductRepository
import com.inikasir.data.repository.TransactionRepository
import com.inikasir.domain.usecase.product.AddProductUseCase
import com.inikasir.domain.usecase.product.DeleteProductUseCase
import com.inikasir.domain.usecase.product.GetAllProductsUseCase
import com.inikasir.domain.usecase.product.UpdateProductUseCase
import com.inikasir.domain.usecase.transaction.GetAllTransactionsUseCase
import com.inikasir.domain.usecase.transaction.GetRecapsUseCase
import com.inikasir.presentation.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AdminViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val getRecapsUseCase: GetRecapsUseCase,
    private val transactionRepository: TransactionRepository,
    private val productRepository: ProductRepository
) : BaseViewModel() {

    // Products (main products only for display)
    val products = getAllProductsUseCase()
        .map { entities ->
            entities.map { entity ->
                ProductEntity(
                    id = entity.id,
                    name = entity.name,
                    price = entity.price,
                    stock = entity.stock,
                    parentId = entity.parentId,
                    variantName = entity.variantName
                )
            }
        }
        .asLiveData()

    // All Transactions (for history)
    val transactions = getAllTransactionsUseCase()
        .map { entities ->
            entities.map { entity ->
                TransactionEntity(
                    id = entity.id,
                    total = entity.total,
                    date = entity.date,
                    isRecapped = entity.isRecapped,
                    recapId = entity.recapId
                )
            }
        }
        .asLiveData()

    // Recaps history
    val recaps = getRecapsUseCase()
        .asLiveData()

    // UI State
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    // Unrecapped Transactions
    private val _unrecappedTransactions = MutableLiveData<List<TransactionEntity>>()
    val unrecappedTransactions: LiveData<List<TransactionEntity>> = _unrecappedTransactions

    // Recap Result
    private val _recapResult = MutableLiveData<RecapResult>()
    val recapResult: LiveData<RecapResult> = _recapResult

    init {
        loadUnrecappedTransactions()
    }

    fun addProduct(name: String, price: Double, stock: Int = 0, parentId: Long? = null, variantName: String? = null) {
        if (name.isBlank() || price <= 0) {
            _message.value = "Nama dan harga harus valid"
            return
        }

        launch {
            try {
                _isLoading.postValue(true)
                addProductUseCase(name, price, stock, parentId, variantName)
                _message.postValue("Produk berhasil ditambahkan")
            } catch (e: Exception) {
                _message.postValue("Gagal menambah produk: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateProduct(id: Long, name: String, price: Double, stock: Int, parentId: Long? = null, variantName: String? = null) {
        if (name.isBlank() || price <= 0) {
            _message.value = "Nama dan harga harus valid"
            return
        }

        launch {
            try {
                _isLoading.postValue(true)
                updateProductUseCase(id, name, price, stock, parentId, variantName)
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

    fun loadUnrecappedTransactions() {
        launch {
            transactionRepository.getUnrecappedTransactions().collect { transactions ->
                _unrecappedTransactions.postValue(transactions)
            }
        }
    }

    fun createRecap() {
        launch {
            try {
                _isLoading.postValue(true)
                val recapId = transactionRepository.createRecap()
                if (recapId > 0) {
                    val totalRevenue = _unrecappedTransactions.value?.sumOf { it.total } ?: 0.0
                    val count = _unrecappedTransactions.value?.size ?: 0
                    _recapResult.postValue(RecapResult.Success(recapId, count, totalRevenue))
                    // Don't call loadUnrecappedTransactions here - let the Fragment handle it after user dismisses dialog
                } else {
                    _recapResult.postValue(RecapResult.Error("Tidak ada transaksi untuk direkap"))
                }
            } catch (e: Exception) {
                _recapResult.postValue(RecapResult.Error(e.message ?: "Gagal membuat rekap"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun getTransactionDetail(transactionId: Long, callback: (TransactionEntity?, List<TransactionDetailWithProduct>) -> Unit) {
        launch {
            val (transaction, details) = transactionRepository.getTransactionWithDetails(transactionId)
            withContext(Dispatchers.Main) {
                callback(transaction, details)
            }
        }
    }

    fun getMainProducts(callback: (List<ProductEntity>) -> Unit) {
        launch {
            productRepository.getMainProducts().collect { products ->
                withContext(Dispatchers.Main) {
                    callback(products)
                }
            }
        }
    }

    fun getVariants(parentId: Long, callback: (List<ProductEntity>) -> Unit) {
        launch {
            productRepository.getVariants(parentId).collect { variants ->
                withContext(Dispatchers.Main) {
                    callback(variants)
                }
            }
        }
    }

    sealed class RecapResult {
        data class Success(val recapId: Long, val transactionCount: Int, val totalRevenue: Double) : RecapResult()
        data class Error(val message: String) : RecapResult()
    }

    fun clearMessage() {
        _message.value = ""
    }
}