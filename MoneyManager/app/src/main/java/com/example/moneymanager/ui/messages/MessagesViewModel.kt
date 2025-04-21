package com.example.moneymanager.ui.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneymanager.message.Message

class MessagesViewModel : ViewModel() {

    // LiveData for holding the list of messages
    private val _messages = MutableLiveData<List<Message>>().apply {
        value = emptyList()
    }
    val messages: LiveData<List<Message>> = _messages

    // LiveData to track new messages count (if needed)
    private val _newMessages = MutableLiveData<Int>().apply {
        value = 0
    }
    val newMessages: LiveData<Int> = _newMessages

    // Method to update the messages list
    fun setMessages(messages: List<Message>) {
        _messages.value = messages
    }

    // Method to update new message count (if you need to track this)
    fun setNewMessages(newMessages: Int) {
        _newMessages.value = newMessages
    }
}