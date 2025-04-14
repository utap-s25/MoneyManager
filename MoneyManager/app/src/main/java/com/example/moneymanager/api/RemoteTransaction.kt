package edu.cs371m.budget.api

data class RemoteTransaction(
    val guid: String,
    val amount: Double,
    val date: String,
    val description: String,
    val category: String,  // category field is here
    val account_guid: String,
    val is_pending: Boolean
)
