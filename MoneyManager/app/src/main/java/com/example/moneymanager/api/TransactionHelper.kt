package com.example.moneymanager.api

import android.util.Log
import com.example.moneymanager.database.LocalTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TransactionHelper {

    suspend fun setupAndFetchTestTransactions(api: TransactionApi, userGuid: String): List<LocalTransaction> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("TransactionHelper", "Using user GUID: $userGuid")
                // Hardcoded user and member GUIDs (already created)
                val memberGuid = "MBR-2110f075-9767-4ae8-b295-9834e79dd11b" // replace with your real member GUID

                // Optional: Trigger aggregation if needed
                api.aggregateMember(userGuid, memberGuid)

                // Optional: Wait to allow aggregation to process
                delay(3000)

                // Fetch transactions
                val transactionsResponse = api.getTransactionsByMember(userGuid, memberGuid)

                // Map to LocalTransaction list
                transactionsResponse.transactions?.map {
                    LocalTransaction(
                        id = 0,
                        amount = it.amount,
                        description = it.description ?: "No Description",
                        date = it.date,
                        category = it.category ?: "Unknown",
                        guid = it.guid
                    )
                } ?: emptyList()

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
