package com.inikasir.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Double,
    val stock: Int = 0,
    val parentId: Long? = null, // null = produk utama, tidak null = varian
    val variantName: String? = null // nama varian (contoh: "Rasa Strawberry")
)