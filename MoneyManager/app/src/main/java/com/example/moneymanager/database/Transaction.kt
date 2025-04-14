package com.example.moneymanager.database

data class Transaction(
    val id: Int,
    val amount: Double,
    val description: String,
    val date: Long,
    val type: String
)