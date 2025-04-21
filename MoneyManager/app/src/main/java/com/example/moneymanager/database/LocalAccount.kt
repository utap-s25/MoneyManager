package com.example.moneymanager.database

data class LocalAccount (
    val id: Int,
    val guid: String,
    val type: String,
    val balance: Double
)