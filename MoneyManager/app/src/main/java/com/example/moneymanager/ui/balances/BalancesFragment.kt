package com.example.moneymanager.ui.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.databinding.FragmentBalancesBinding
import com.example.moneymanager.repositories.Accounts // assuming this is your repo class

class BalancesFragment : Fragment() {

    private var _binding: FragmentBalancesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBalancesBinding.inflate(inflater, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.balancesRecyclerView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                bottom = systemBars.bottom + 16 // 16dp padding + space for nav bar
            )
            WindowInsetsCompat.CONSUMED
        }
        val root: View = binding.root

        val accountsRepo = Accounts(requireContext())
        val accounts = accountsRepo.getAllAccounts() // this should return List<LocalAccount>

        // Set up RecyclerView
        val adapter = AccountBalanceAdapter(accounts)
        binding.balancesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.balancesRecyclerView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
