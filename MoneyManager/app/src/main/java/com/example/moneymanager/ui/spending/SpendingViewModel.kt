package com.example.moneymanager.ui.spending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpendingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Spending Fragment"
    }
    // LiveData to track total spending
    private val _totalSpending = MutableLiveData<Float>().apply {
        value = 0f  // Default value
    }
    var totalSpending: LiveData<Float> = _totalSpending

    // LiveData to track the top 4 highest spending categories
    private val _topSpendingCategories = MutableLiveData<List<SpendingCategory>>().apply {
        value = emptyList()
    }
    val topSpendingCategories: LiveData<List<SpendingCategory>> = _topSpendingCategories

    // LiveData for miscellaneous spending (the sum of categories not in the top 4)
    private val _miscellaneousSpending = MutableLiveData<Float>().apply {
        value = 0f  // Default value
    }
    val miscellaneousSpending: LiveData<Float> = _miscellaneousSpending

    fun updateTotalSpending(total: Float) {
        _totalSpending.value = total
    }
    // Function to update the spending categories
    fun updateSpendingCategories(categories: List<SpendingCategory>) {
        // Sort categories in descending order based on the amount
        val sortedCategories = categories.sortedByDescending { it.amount }

        // Top 4 categories
        val top4 = sortedCategories.take(4)

        // Miscellaneous spending is the sum of all categories not in the top 4
        val miscellaneous = sortedCategories.drop(4).sumOf { it.amount.toDouble() }.toFloat()

        // Update LiveData
        _topSpendingCategories.value = top4
        _miscellaneousSpending.value = miscellaneous

        // Update total spending (sum of all categories)
        val total = sortedCategories.sumOf { it.amount.toDouble() }.toFloat()
        _totalSpending.value = total
    }
}

data class SpendingCategory(val name: String, val amount: Float)