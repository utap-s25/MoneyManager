package com.example.moneymanager.message

data class Message(
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    @get:com.google.firebase.firestore.PropertyName("isRead")
    @set:com.google.firebase.firestore.PropertyName("isRead")
    var isRead: Boolean = false
)