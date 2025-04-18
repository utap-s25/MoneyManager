package com.example.moneymanager.ui.balances

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BalancesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Balances Fragment"
    }
    val text: LiveData<String> = _text

    private val _totalBalance = MutableLiveData<Float>().apply {
        value = 0f
    }

    fun setTotalBalance(totalBalance: Float) {
        _totalBalance.value = totalBalance
    }

    val totalBalance: LiveData<Float> = _totalBalance
}