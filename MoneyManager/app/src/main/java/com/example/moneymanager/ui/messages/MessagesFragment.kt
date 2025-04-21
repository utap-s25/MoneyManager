package com.example.moneymanager.ui.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.moneymanager.databinding.FragmentMessagesBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.message.Message
import com.example.moneymanager.message.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MessageAdapter
    private val messages = mutableListOf<Message>()

    private var receiverId: String = "SWV6Vp1NeAVoAgKBjC1cM2iD9E13"
    private var currentUserId: String? = null
    private lateinit var conversationId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        conversationId = if (currentUserId!! < receiverId) {
            "$currentUserId-$receiverId"
        } else {
            "$receiverId-$currentUserId"
        }

        adapter = MessageAdapter(messages, currentUserId!!)
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMessages.adapter = adapter

        listenForMessages()

        binding.sendButton.setOnClickListener {
            val text = binding.messageEditText.text.toString()
            if (text.isNotBlank()) {
                sendMessage(text)
                binding.messageEditText.setText("")
            }
        }
    }

    private fun sendMessage(messageText: String) {
        val message = hashMapOf(
            "senderId" to currentUserId,
            "receiverId" to receiverId,
            "message" to messageText,
            "timestamp" to System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .add(message)
    }

    private fun listenForMessages() {
        FirebaseFirestore.getInstance()
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && binding != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        doc.toObject(Message::class.java)?.let { messages.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                    binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}