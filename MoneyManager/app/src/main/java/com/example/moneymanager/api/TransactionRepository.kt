package com.example.moneymanager.api

import com.example.moneymanager.database.LocalTransaction as LocalTransaction
import edu.cs371m.budget.api.TransactionApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TransactionRepository(private val transactionApi: TransactionApi) {

    suspend fun getTransactionsByMember(userGuid: String, memberGuid: String): List<LocalTransaction> {
        return withContext(Dispatchers.IO) {
            try {
                val response = transactionApi.getTransactionsByMember(userGuid, memberGuid)
                // Convert the API's transaction response to the local DB format
                response.transactions.map { remoteTransaction ->
                    LocalTransaction(
                        id = 0, // Placeholder
                        amount = remoteTransaction.amount,
                        description = remoteTransaction.description ?: "No Description",
                        date = remoteTransaction.date,
                        category = remoteTransaction.category ?: "Unknown" // Accessing category here
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // Return empty list in case of error
            }
        }
    }
}
