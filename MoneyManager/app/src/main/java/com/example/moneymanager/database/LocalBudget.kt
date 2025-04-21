package com.example.moneymanager.database

data class LocalBudget(
    val id: Int,
    val guid: String,
    val name: String,
    val percentSpent: Double,
    val amount: Double,
    val categoryId: String
)