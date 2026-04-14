package com.inikasir

import android.app.Application
import com.inikasir.data.local.AppDatabase
import com.inikasir.data.local.entity.ProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InikasirApplication : Application() {
    
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Insert sample products
        CoroutineScope(Dispatchers.IO).launch {
            val productDao = database.productDao()
            val count = productDao.getProductCount()
            if (count == 0) {
                val sampleProducts = listOf(
                    ProductEntity(name = "Kopi Hitam", price = 15000.0),
                    ProductEntity(name = "Kopi Susu", price = 18000.0),
                    ProductEntity(name = "Cappuccino", price = 20000.0),
                    ProductEntity(name = "Teh Tarik", price = 12000.0),
                    ProductEntity(name = "Roti Bakar", price = 10000.0),
                    ProductEntity(name = "Kentang Goreng", price = 15000.0),
                    ProductEntity(name = "Nasi Goreng", price = 25000.0),
                    ProductEntity(name = "Mie Goreng", price = 20000.0)
                )
                sampleProducts.forEach { productDao.insert(it) }
            }
        }
    }
    
    companion object {
        lateinit var instance: InikasirApplication
            private set
    }
}