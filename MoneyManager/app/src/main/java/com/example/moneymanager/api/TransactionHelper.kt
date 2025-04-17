package com.example.moneymanager.api

import com.example.moneymanager.database.LocalTransaction
import com.example.moneymanager.api.TransactionApi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class TransactionHelper {

    suspend fun setupAndFetchTestTransactions(api: TransactionApi): List<LocalTransaction> {
        return withContext(Dispatchers.IO) {
            try {
                // Step 1: Create a test user
                val userResponse = api.createUser(
                    TransactionApi.CreateUserBody(mapOf("id" to "test_user_${System.currentTimeMillis()}"))
                )
                val userGuid = userResponse.user.guid

                // Step 2: Get credentials for mxbank
                val institution = api.getInstitution("mxbank")
                val credentials = institution.institution.credentials

                // Build fake login credentials
                val fakeCreds = credentials.map {
                    mapOf(
                        "guid" to it.guid,
                        "value" to when (it.displayName.lowercase()) {
                            "username" -> "test_user"
                            "password" -> "password"
                            else -> "test_value"
                        }
                    )
                }

                // Step 3: Create test member
                val memberResponse = api.createTestMember(
                    userGuid,
                    TransactionApi.CreateMemberBody(
                        mapOf(
                            "institution_code" to "mxbank",
                            "credentials" to fakeCreds
                        )
                    )
                )
                val memberGuid = memberResponse.member.guid

                // Step 4: Trigger aggregation
                api.aggregateMember(userGuid, memberGuid)

                // Optional: Wait for a few seconds if you want to ensure aggregation completes
                delay(3000)

                // Step 5: Fetch transactions
                val transactionsResponse = api.getTransactionsByMember(userGuid, memberGuid)

                // Check if transactionsResponse is valid and contains transactions
                transactionsResponse.transactions?.let {
                    // Convert to LocalTransaction
                    return@withContext it.map {
                        LocalTransaction(
                            id = 0,
                            amount = it.amount,
                            description = it.description ?: "No Description",
                            date = it.date,
                            category = it.category ?: "Unknown"
                        )
                    }
                } ?: run {
                    // Return empty list if transactions are null or empty
                    return@withContext emptyList<LocalTransaction>()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }


}