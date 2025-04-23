package com.example.moneymanager.ui.messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentMessagesBinding
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

    private lateinit var receiverId: String
    private var currentUserId: String? = null
    private lateinit var conversationId: String

    // Use ViewModel to store data
    private val viewModel: MessagesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the current user ID
        receiverId = context?.getString(R.string.financial_advisor_id) ?: ""
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        // TODO IMPLEMENT UI FOR MONEY MANAGER THAT PULLS ALL USERIDS
        // THIS IS FUNCTIONALITY THAT IS EXCLUSIVE FOR THE MONEY MANAGER
        // WE DIDN'T CREATE A SEPERATE UI FOR THE MONEY MANAGER TO KEEP TRACK OF THE MESSAGE COMPONENTS
        // SO WE HARDCODED THIS TO POINT AT OUR TEST USER
        if (currentUserId == receiverId) {
            receiverId = "TxBSjXMdY1dRxVOYN3JM4DGzXT73"
        }
        Log.d("MessagesFragment", "currentUserId: $currentUserId, receiverId: $receiverId")
        conversationId = if (currentUserId!! < receiverId) {
            "$currentUserId-$receiverId"
        } else {
            "$receiverId-$currentUserId"
        }

        // Set up RecyclerView with adapter
        adapter = MessageAdapter(messages, currentUserId!!)
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
            reverseLayout = false
        }
        binding.recyclerViewMessages.adapter = adapter

        // Observe messages from the ViewModel
        viewModel.messages.observe(viewLifecycleOwner, Observer { updatedMessages ->
            messages.clear()
            messages.addAll(updatedMessages)
            adapter.notifyDataSetChanged()
            binding.recyclerViewMessages.scrollToPosition(messages.size - 1)
        })

        // Send button click listener
        binding.sendButton.setOnClickListener {
            val text = binding.messageEditText.text.toString()
            if (text.isNotBlank()) {
                sendMessage(text)
                binding.messageEditText.setText("") // Clear the input field
                FirebaseFirestore.getInstance()
                    .collection("conversations")
                    .document(conversationId!!)
                    .collection("messages")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get() // Add this call to force Firestore to refresh the listener
                    .addOnSuccessListener { snapshot ->
                        Log.d("Firestore", "Snapshot refreshed after sending message")
                    }
            }
        }


    }

    override fun onStart() {
        super.onStart()
        markMessagesAsRead()
    }

    override fun onResume() {
        super.onResume()
        markMessagesAsRead()
    }

    override fun onStop() {
        super.onStop()
        markMessagesAsRead()
    }

    override fun onPause() {
        super.onPause()
        markMessagesAsRead()
    }

    private fun sendMessage(messageText: String) {
        val message = hashMapOf(
            "senderId" to currentUserId,
            "receiverId" to receiverId,
            "message" to messageText,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false
        )

        FirebaseFirestore.getInstance()
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .add(message)
    }

    private fun markMessagesAsRead() {
        Log.d("MessageListener", "GOING TO MARK ALL MESSAGES AS UNREAD")
        FirebaseFirestore.getInstance()
            .collection("conversations")
            .document(conversationId)
            .collection("messages")
            .whereEqualTo("receiverId", currentUserId)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    doc.reference.update("isRead", true)
                }
                viewModel.setNewMessages(0) // Reset LiveData
                Log.d("MessagesFragment", "Marked ${snapshot.size()} messages as read")
            }
            .addOnFailureListener { e ->
                Log.e("MessagesFragment", "Failed to mark messages as read", e)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}