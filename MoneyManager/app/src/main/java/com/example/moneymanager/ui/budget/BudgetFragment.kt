package com.example.moneymanager.ui.budget

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentBudgetBinding
import com.example.moneymanager.api.BudgetsApi
import com.example.moneymanager.api.BudgetsApi.Budget
import com.example.moneymanager.api.BudgetsApi.BudgetsResponse
import com.example.moneymanager.api.BudgetsHelper
import com.example.moneymanager.api.TransactionApi
import com.example.moneymanager.databinding.FragmentDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.moneymanager.ui.budget.BudgetsAdapter

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val budgetViewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 1. Function to update the RecyclerView with the list of budgets
        fun updateRecycler(budgets: List<Budget>) {
            val totalAmount = budgets.sumOf { it.amount }
            for (budget in budgets) {
                println("Budget Name: ${budget.name}")
                println("Budget Amount: ${budget.amount}")
                println("Percent Spent: ${budget.percent_spent}")
            }
            println()
            val adapter = BudgetsAdapter(budgets, "$%.2f".format(totalAmount))
            binding.budgetsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.budgetsRecyclerView.adapter = adapter
        }

        // 2. Fetch budgets from API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // TEST DATA REMOVE AFTER
                val api = BudgetsApi.create()
                val budgetsHelper = BudgetsHelper()
                val budgets = budgetsHelper.setupAndFetchTestTransactions(api)

                withContext(Dispatchers.Main) {
                    if (budgets.isNotEmpty()) {
                        updateRecycler(budgets)
                    } else {
                        Log.d("BudgetFragment", "No budgets found")
                    }
                }
            } catch (e: Exception) {
                Log.e("BudgetFragment", "Error fetching budgets: ${e.message}")
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}