package com.example.moneymanager.ui.budget

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.api.BudgetsApi
import com.example.moneymanager.databinding.FragmentBudgetBinding
import com.example.moneymanager.repositories.Budget // Assuming this is your local budgets repository
import kotlinx.coroutines.launch

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)

        // Apply bottom padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.budgetsRecyclerView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom + 16)
            WindowInsetsCompat.CONSUMED
        }

        val root: View = binding.root

        val budgetsRepo = Budget(requireContext())
        val budgets = budgetsRepo.getAllBudgets() // This should return List<LocalBudget>

        // Set up RecyclerView
        val adapter = BudgetsAdapter(budgets)
        binding.budgetsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.budgetsRecyclerView.adapter = adapter

        binding.createNewBudgetButton.setOnClickListener {
            // --- New behavior: Navigate to Create Budget Form ---
            findNavController().navigate(R.id.navigation_create_budget)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
