package com.example.moneymanager.api

import android.util.Log
import com.example.moneymanager.api.BudgetsApi.Budget
import com.example.moneymanager.api.BudgetsApi.BudgetsResponse
import com.example.moneymanager.database.LocalTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class BudgetsHelper {

    suspend fun setupAndFetchTestTransactions(api: BudgetsApi): List<Budget> {
        return withContext(Dispatchers.IO) {

            try {
                // Hardcoded user and member GUIDs (already created)
                val userGuid = "USR-57750651-ce65-4480-9999-fc57ed8b805a"

                // Optional: Wait to allow aggregation to process
                delay(3000)

                // Fetch transactions
                val budgetsResponse = api.getBudgetsByUser(userGuid)

                // Map to LocalTransaction list
                budgetsResponse.budgets
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
