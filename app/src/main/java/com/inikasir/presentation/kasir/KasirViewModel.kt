package com.inikasir.presentation.kasir

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.domain.model.CartItem
import com.inikasir.domain.usecase.product.GetAllProductsUseCase
import com.inikasir.domain.usecase.transaction.CreateTransactionUseCase
import com.inikasir.presentation.common.BaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KasirViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val createTransactionUseCase: CreateTransactionUseCase
) : BaseViewModel() {

    // Products
    val products = getAllProductsUseCase()
        .map { entities ->
            // Only show main products (parentId == null) in the grid
            entities.filter { it.parentId == null }
        }
        .asLiveData()

    // Cart
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalAmount = MutableLiveData(0.0)
    val totalAmount: LiveData<Double> = _totalAmount

    private val _transactionResult = MutableLiveData<TransactionResult>()
    val transactionResult: LiveData<TransactionResult> = _transactionResult

    // Search
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    fun handleProductClick(product: ProductEntity) {
        // Check if product has variants by checking if there are any children
        // For now, we'll let the Fragment handle showing the variant dialog
        // This method can be used for future logic if needed
    }

    fun getVariants(parentId: Long, callback: (List<ProductEntity>) -> Unit) {
        launch {
            // Get all products and filter for variants of this parent
            val allProducts = getAllProductsUseCase().value ?: emptyList()
            val variants = allProducts.filter { it.parentId == parentId }
            withContext(Dispatchers.Main) {
                callback(variants)
            }
        }
    }

    fun addToCart(product: ProductEntity) {
        addItemToCart(product)
    }

    private fun addItemToCart(product: ProductEntity) {
        val currentCart = _cartItems.value?.toMutableList() ?: mutableListOf()
        val existingItem = currentCart.find { it.productId == product.id }

        if (existingItem != null) {
            val index = currentCart.indexOf(existingItem)
            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + 1,
                subtotal = (existingItem.quantity + 1) * existingItem.price
            )
            currentCart[index] = updatedItem
        } else {
            currentCart.add(
                CartItem(
                    productId = product.id,
                    productName = if (product.variantName != null) {
                        "${product.name} (${product.variantName})"
                    } else {
                        product.name
                    },
                    price = product.price,
                    quantity = 1,
                    subtotal = product.price
                )
            )
        }

        _cartItems.value = currentCart
        calculateTotal()
    }

    fun updateQuantity(productId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(productId)
            return
        }

        val currentCart = _cartItems.value?.toMutableList() ?: return
        val index = currentCart.indexOfFirst { it.productId == productId }

        if (index != -1) {
            val item = currentCart[index]
            currentCart[index] = item.copy(
                quantity = newQuantity,
                subtotal = newQuantity * item.price
            )
            _cartItems.value = currentCart
            calculateTotal()
        }
    }

    fun removeFromCart(productId: Long) {
        val currentCart = _cartItems.value?.toMutableList() ?: return
        currentCart.removeAll { it.productId == productId }
        _cartItems.value = currentCart
        calculateTotal()
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _totalAmount.value = 0.0
    }

    private fun calculateTotal() {
        val total = _cartItems.value?.sumOf { it.subtotal } ?: 0.0
        _totalAmount.value = total
    }

    fun checkout() {
        val items = _cartItems.value ?: emptyList()
        val total = _totalAmount.value ?: 0.0

        if (items.isEmpty()) {
            _transactionResult.value = TransactionResult.Error("Keranjang kosong")
            return
        }

        launch {
            try {
                val transactionId = createTransactionUseCase(items, total)
                clearCart() // Clear cart AFTER successful transaction
                _transactionResult.postValue(TransactionResult.Success(transactionId, total))
            } catch (e: Exception) {
                _transactionResult.postValue(TransactionResult.Error(e.message ?: "Transaksi gagal"))
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    sealed class TransactionResult {
        data class Success(val transactionId: Long, val totalAmount: Double) : TransactionResult()
        data class Error(val message: String) : TransactionResult()
    }
}