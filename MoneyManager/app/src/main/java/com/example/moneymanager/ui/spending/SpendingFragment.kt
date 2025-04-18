package com.example.moneymanager.ui.spending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.databinding.FragmentSpendingBinding
import com.example.moneymanager.repositories.Transaction as TransactionRepo

class SpendingFragment : Fragment() {

    private var _binding: FragmentSpendingBinding? = null
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

        // Fetch from local DB
        val transactionRepo = TransactionRepo(requireContext())
        val transactions = transactionRepo.getAllTransactions()

        // Setup RecyclerView
        val recyclerView = binding.transactionsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = TransactionAdapter(transactions)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
