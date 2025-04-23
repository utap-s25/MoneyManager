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

    fun updateTotalSpending(total: Float) {
        _totalSpending.value = total
    }
}

data class SpendingCategory(val name: String, val amount: Float)