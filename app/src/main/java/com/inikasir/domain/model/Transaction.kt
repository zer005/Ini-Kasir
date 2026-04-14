package com.inikasir.domain.model

data class Transaction(
    val id: Long = 0,
    val total: Double,
    val date: Long = System.currentTimeMillis(),
    val items: List<TransactionDetail> = emptyList()
)