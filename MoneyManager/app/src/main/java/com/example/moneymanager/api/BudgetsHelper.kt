package com.example.moneymanager.api

import com.example.moneymanager.database.LocalBudget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class BudgetsHelper {

    suspend fun setupAndFetchBudgets(api: BudgetsApi, userGuid: String): List<LocalBudget> {
        return withContext(Dispatchers.IO) {
            try {
                // Optional: Wait to allow aggregation to process
                delay(3000)

                // Fetch budgets from the API
                val budgetsResponse = api.getBudgetsByUser(userGuid)

                // Map API budget objects to LocalBudget
                budgetsResponse.budgets.map {
                    LocalBudget(
                        id = 0,  // DB auto-generates this
                        guid = it.guid,
                        name = it.name,
                        amount = it.amount,
                        percentSpent = it.percent_spent,
                        categoryId = it.category_guid
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
