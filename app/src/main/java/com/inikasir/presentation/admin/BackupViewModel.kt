package com.inikasir.presentation.admin

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.inikasir.data.local.AppDatabase
import com.inikasir.data.local.entity.ProductEntity
import com.inikasir.data.local.entity.TransactionDetailEntity
import com.inikasir.data.local.entity.TransactionEntity
import com.inikasir.presentation.common.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class BackupViewModel(
    private val context: Context,
    private val database: AppDatabase
) : BaseViewModel() {
    
    private val _backupResult = MutableLiveData<BackupResult>()
    val backupResult: LiveData<BackupResult> = _backupResult
    
    private val _restoreResult = MutableLiveData<RestoreResult>()
    val restoreResult: LiveData<RestoreResult> = _restoreResult
    
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    fun exportToCSV() {
        launch {
            try {
                _isLoading.postValue(true)
                
                val backupDir = File(context.getExternalFilesDir(null), "backup")
                if (!backupDir.exists()) {
                    backupDir.mkdirs()
                }
                
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                
                // Export Products
                val productsFile = File(backupDir, "products_$timestamp.csv")
                exportProducts(productsFile)
                
                // Export Transactions
                val transactionsFile = File(backupDir, "transactions_$timestamp.csv")
                exportTransactions(transactionsFile)
                
                // Export Transaction Details
                val detailsFile = File(backupDir, "transaction_details_$timestamp.csv")
                exportTransactionDetails(detailsFile)
                
                _backupResult.postValue(
                    BackupResult.Success(
                        productsFile.absolutePath,
                        transactionsFile.absolutePath,
                        detailsFile.absolutePath
                    )
                )
                
            } catch (e: Exception) {
                _backupResult.postValue(BackupResult.Error(e.message ?: "Backup gagal"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    private suspend fun exportProducts(file: File) {
        withContext(Dispatchers.IO) {
            val products = database.productDao().getAllProductsSync()
            
            FileWriter(file).use { writer ->
                // Header
                writer.write("id,name,price\n")
                
                // Data
                products.forEach { product ->
                    writer.write("${product.id},${product.name},${product.price}\n")
                }
            }
        }
    }
    
    private suspend fun exportTransactions(file: File) {
        withContext(Dispatchers.IO) {
            val transactions = database.transactionDao().getAllTransactionsSync()
            
            FileWriter(file).use { writer ->
                // Header
                writer.write("id,total,date\n")
                
                // Data
                transactions.forEach { transaction ->
                    writer.write("${transaction.id},${transaction.total},${transaction.date}\n")
                }
            }
        }
    }
    
    private suspend fun exportTransactionDetails(file: File) {
        withContext(Dispatchers.IO) {
            val details = database.transactionDetailDao().getAllDetailsSync()
            
            FileWriter(file).use { writer ->
                // Header
                writer.write("id,transaction_id,product_id,quantity,subtotal\n")
                
                // Data
                details.forEach { detail ->
                    writer.write("${detail.id},${detail.transactionId},${detail.productId},${detail.quantity},${detail.subtotal}\n")
                }
            }
        }
    }
    
    fun importFromCSV(productsFile: File?, transactionsFile: File?, detailsFile: File?) {
        launch {
            try {
                _isLoading.postValue(true)
                
                var importedProducts = 0
                var importedTransactions = 0
                var importedDetails = 0
                
                productsFile?.let {
                    importedProducts = importProducts(it)
                }
                
                transactionsFile?.let {
                    importedTransactions = importTransactions(it)
                }
                
                detailsFile?.let {
                    importedDetails = importTransactionDetails(it)
                }
                
                _restoreResult.postValue(
                    RestoreResult.Success(importedProducts, importedTransactions, importedDetails)
                )
                
            } catch (e: Exception) {
                _restoreResult.postValue(RestoreResult.Error(e.message ?: "Restore gagal"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
    
    private suspend fun importProducts(file: File): Int {
        return withContext(Dispatchers.IO) {
            var count = 0
            file.bufferedReader().useLines { lines ->
                lines.drop(1).forEach { line ->  // Skip header
                    val parts = line.split(",")
                    if (parts.size >= 3) {
                        val id = parts[0].toLongOrNull() ?: 0L
                        val name = parts[1]
                        val price = parts[2].toDoubleOrNull() ?: 0.0
                        
                        if (name.isNotBlank() && price > 0) {
                            val product = ProductEntity(id = id, name = name, price = price)
                            database.productDao().insert(product)
                            count++
                        }
                    }
                }
            }
            count
        }
    }
    
    private suspend fun importTransactions(file: File): Int {
        return withContext(Dispatchers.IO) {
            var count = 0
            file.bufferedReader().useLines { lines ->
                lines.drop(1).forEach { line ->
                    val parts = line.split(",")
                    if (parts.size >= 3) {
                        val id = parts[0].toLongOrNull() ?: 0L
                        val total = parts[1].toDoubleOrNull() ?: 0.0
                        val date = parts[2].toLongOrNull() ?: System.currentTimeMillis()
                        
                        val transaction = TransactionEntity(id = id, total = total, date = date)
                        database.transactionDao().insert(transaction)
                        count++
                    }
                }
            }
            count
        }
    }
    
    private suspend fun importTransactionDetails(file: File): Int {
        return withContext(Dispatchers.IO) {
            var count = 0
            file.bufferedReader().useLines { lines ->
                lines.drop(1).forEach { line ->
                    val parts = line.split(",")
                    if (parts.size >= 5) {
                        val id = parts[0].toLongOrNull() ?: 0L
                        val transactionId = parts[1].toLongOrNull() ?: 0L
                        val productId = parts[2].toLongOrNull() ?: 0L
                        val quantity = parts[3].toIntOrNull() ?: 1
                        val subtotal = parts[4].toDoubleOrNull() ?: 0.0
                        
                        val detail = TransactionDetailEntity(
                            id = id,
                            transactionId = transactionId,
                            productId = productId,
                            quantity = quantity,
                            subtotal = subtotal
                        )
                        database.transactionDetailDao().insert(detail)
                        count++
                    }
                }
            }
            count
        }
    }
    
    sealed class BackupResult {
        data class Success(
            val productsPath: String,
            val transactionsPath: String,
            val detailsPath: String
        ) : BackupResult()
        data class Error(val message: String) : BackupResult()
    }
    
    sealed class RestoreResult {
        data class Success(
            val productsCount: Int,
            val transactionsCount: Int,
            val detailsCount: Int
        ) : RestoreResult()
        data class Error(val message: String) : RestoreResult()
    }
}