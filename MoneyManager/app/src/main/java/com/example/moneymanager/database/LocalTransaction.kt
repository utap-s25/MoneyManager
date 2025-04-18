package com.example.moneymanager.database

data class LocalTransaction(
    val id: Int,
    val amount: Double,
    val description: String,
    val date: String,
    val category: String,
    val guid: String
)