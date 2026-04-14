package com.inikasir.domain.model

data class TransactionDetail(
    val id: Long = 0,
    val transactionId: Long,
    val productId: Long,
    val productName: String = "",
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)