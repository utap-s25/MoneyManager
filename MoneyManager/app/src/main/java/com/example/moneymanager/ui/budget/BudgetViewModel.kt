package com.example.moneymanager.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BudgetViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Budget Fragment"
    }
    val text: LiveData<String> = _text

    private val _totalBudget = MutableLiveData<Float>().apply {
        value = 0f  // Default value for total budget
    }

    private val _spending = MutableLiveData<Float>().apply {
        value = 0f  // Default value for spending
    }

    private val _remainingBudget = MediatorLiveData<Float>().apply {
        // Listen for changes in _totalBudget and _spending
        addSource(_totalBudget) { totalBudget ->
            updateRemainingBudget(totalBudget, _spending.value ?: 0f)
        }
        addSource(_spending) { spending ->
            updateRemainingBudget(_totalBudget.value ?: 0f, spending)
        }
    }

    val remainingBudget: LiveData<Float> = _remainingBudget

    // Function to update the total budget
    fun setTotalBudget(totalBudget: Float) {
        _totalBudget.value = totalBudget
    }

    // Function to update spending
    fun updateSpending(spending: Float) {
        _spending.value = spending
    }

    // This is internal logic to update the remaining budget
    private fun updateRemainingBudget(totalBudget: Float, spending: Float) {
        _remainingBudget.value = totalBudget - spending
    }
}