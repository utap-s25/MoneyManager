package com.example.moneymanager.api

import android.util.Log
import com.example.moneymanager.database.LocalTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionHelper {

    suspend fun setupAndFetchTestTransactions(api: TransactionApi): List<LocalTransaction> {
        return withContext(Dispatchers.IO) {
            try {
                val userGuid = "USR-9cd24e37-15f6-4938-958a-7f0798e63c3c"
                val memberGuid = "MBR-e7cce61b-4f7f-4651-add3-030bd3f25858"

                api.aggregateMember(userGuid, memberGuid)
                delay(3000)

                val response = api.getTransactionsByMember(userGuid, memberGuid)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                // build & return your list here
                return@withContext response.transactions
                    ?.map { remote ->
                        val parsedDate = try {
                            formatter.parse(remote.date)?.time
                                ?: System.currentTimeMillis()
                        } catch (e: Exception) {
                            Log.e("TransactionHelper",
                                "Failed to parse date: ${remote.date}", e)
                            System.currentTimeMillis()
                        }
                        Log.d("TransactionHelper",
                            "Parsed date: ${remote.date} -> $parsedDate")

                        LocalTransaction(
                            id          = 0,
                            amount      = remote.amount,
                            description = remote.description ?: "No Description",
                            date        = parsedDate,
                            category    = remote.category ?: "Unknown",
                            guid        = remote.guid
                        )
                    }
                    ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext emptyList()
            }
        }
    }
}

