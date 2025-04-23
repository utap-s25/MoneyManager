package com.example.moneymanager.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentBudgetBinding
import com.example.moneymanager.repositories.Budget

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var budgetsAdapter: BudgetsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.budgetsRecyclerView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom + 16)
            WindowInsetsCompat.CONSUMED
        }

        val root: View = binding.root

        val budgetsRepo = Budget(requireContext())
        val budgets = budgetsRepo.getAllBudgets().toMutableList() // 🔥 Make it mutable

        // 🔥 Create adapter with deletion callback
        budgetsAdapter = BudgetsAdapter(budgets) { guid, position ->
            lifecycleScope.launch {
                try {
                    BudgetsApi.create().deleteBudget("user-guid", guid)
                } catch (e: Exception) {
                    Log.e("BudgetFragment", "Failed to delete budget $guid: ${e.message}")
                } finally {
                    budgetsRepo.deleteBudgetByGuid(guid) // 🔥 Add this to your Budget repo

                    budgets.removeAt(position)
                    budgetsAdapter.notifyItemRemoved(position)
                }
            }
        }


        binding.budgetsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.budgetsRecyclerView.adapter = budgetsAdapter

        binding.createNewBudgetButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_create_budget)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

