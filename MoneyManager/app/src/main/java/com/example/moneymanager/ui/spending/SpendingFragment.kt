package com.example.moneymanager.ui.spending

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.databinding.FragmentSpendingBinding
import com.example.moneymanager.api.TransactionRepository
import com.example.moneymanager.api.TransactionApi
import com.example.moneymanager.api.TransactionHelper
import kotlinx.coroutines.launch

class SpendingFragment : Fragment() {

    private var _binding: FragmentSpendingBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionRepository: TransactionRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val spendingViewModel =
            ViewModelProvider(this).get(SpendingViewModel::class.java)

        _binding = FragmentSpendingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        transactionRepository = TransactionRepository(TransactionApi.create())
        val transactionHelper = TransactionHelper()
        val api = TransactionApi.create()

        lifecycleScope.launch {
            try {
                val transactions = transactionHelper.setupAndFetchTestTransactions(api)
                Log.d("SpendingFragment", "Fetched ${transactions.size} test transactions")

                val adapter = TransactionAdapter(transactions)
                binding.transactionsRecyclerView.adapter = adapter
                binding.transactionsRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext())

            } catch (e: Exception) {
                Log.e("SpendingFragment", "Error fetching test transactions", e)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
