package com.example.moneymanager.ui.messages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moneymanager.message.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessagesViewModel : ViewModel() {

    private var listenerRegistered = false
    private var conversationId: String? = null

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

    fun startListeningForMessages(currentUserId: String, receiverId: String) {
        if (listenerRegistered) return

        conversationId = if (currentUserId < receiverId) {
            "$currentUserId-$receiverId"
        } else {
            "$receiverId-$currentUserId"
        }

        listenerRegistered = true

        FirebaseFirestore.getInstance()
            .collection("conversations")
            .document(conversationId!!)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val newMessages = mutableListOf<Message>()
                    for (doc in snapshot.documents) {
                        doc.toObject(Message::class.java)?.let { newMessages.add(it) }
                    }

                    _messages.postValue(newMessages)
                }
            }
    }
}