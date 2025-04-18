package com.example.moneymanager.ui.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MessagesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Messages Fragment"
    }
    val text: LiveData<String> = _text

    private val _newMessages = MutableLiveData<Int>().apply {
        value = 0
    }

    fun setNewMessages(newMessages: Int) {
        _newMessages.value = newMessages
    }

    val newMessages: LiveData<Int> = _newMessages
}