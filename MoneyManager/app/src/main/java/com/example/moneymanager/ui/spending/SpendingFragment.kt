package com.example.moneymanager.ui.spending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.moneymanager.databinding.FragmentSpendingBinding
import com.example.moneymanager.api.TransactionRepository
import kotlinx.coroutines.launch

class SpendingFragment : Fragment() {

    private var _binding: FragmentSpendingBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionRepository: TransactionRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val spendingViewModel =
            ViewModelProvider(this).get(SpendingViewModel::class.java)

        _binding = FragmentSpendingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSpending
        spendingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        transactionRepository = TransactionRepository(/* Pass TransactionApi instance here */)

        // Fetch transactions (example usage)
        lifecycleScope.launch {
            val userGuid = "some_user_guid"
            val memberGuid = "some_member_guid"
            val transactions = transactionRepository.getTransactionsByMember(userGuid, memberGuid)
            // Now you can use 'transactions' (e.g., display them in a RecyclerView)
            for (transaction in transactions) {
                println(transaction)  // This will print each transaction's details
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
