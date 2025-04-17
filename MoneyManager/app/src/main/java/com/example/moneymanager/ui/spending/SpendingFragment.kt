package com.example.moneymanager.ui.spending

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.databinding.FragmentSpendingBinding
import com.example.moneymanager.api.TransactionRepository
import com.example.moneymanager.api.TransactionApi
import kotlinx.coroutines.launch
import com.example.moneymanager.api.TransactionHelper
import com.example.moneymanager.ui.spending.TransactionAdapter

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

        val textView: TextView = binding.textSpending
        spendingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        transactionRepository = TransactionRepository(TransactionApi.create())

        val transactionHelper = TransactionHelper()
        val api = TransactionApi.create()

        // Hardcode the user_guid and member_guid for now
        val userGuid = "USR-9cd24e37-15f6-4938-958a-7f0798e63c3c" // Replace with actual user GUID
        val memberGuid = "MBR-e7cce61b-4f7f-4651-add3-030bd3f25858" // Replace with actual member GUID

        // Fetch transactions (example usage)
        lifecycleScope.launch {
            try {
                val transactions = transactionHelper.setupAndFetchTestTransactions(api)
                Log.d("SpendingFragment", "Fetched ${transactions.size} test transactions")

                // Setup RecyclerView with fetched transactions
                val recyclerView = binding.transactionsRecyclerView
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                recyclerView.adapter = TransactionAdapter(transactions)

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
