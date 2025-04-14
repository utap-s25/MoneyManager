package com.example.moneymanager.ui.spending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanager.databinding.FragmentSpendingBinding
import com.example.moneymanager.repositories.TransactionRepository

class SpendingFragment : Fragment() {

    private var _binding: FragmentSpendingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val spendingViewModel =
            ViewModelProvider(this).get(SpendingViewModel::class.java)

        _binding = FragmentSpendingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSpending
        spendingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val repo = TransactionRepository(requireContext())

        // Insert a transaction
        repo.insertTransaction(100.0, "Groceries", System.currentTimeMillis(), "Expense")
        repo.insertTransaction(120.0, "Haircut", System.currentTimeMillis(), "Expense")
        repo.insertTransaction(2400.0, "GPU", System.currentTimeMillis(), "Expense")

        // Get all transactions
        val transactions = repo.getAllTransactions()
        for (transaction in transactions) {
            println(transaction)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}