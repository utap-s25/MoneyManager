package com.example.moneymanager.ui.spending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpendingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Account Fragment"
    }
    val text: LiveData<String> = _text
}