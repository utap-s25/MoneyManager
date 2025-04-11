package com.example.moneymanager.ui.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanager.databinding.FragmentBalancesBinding

class BalancesFragment : Fragment() {

    private var _binding: FragmentBalancesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val balancesViewModel =
            ViewModelProvider(this).get(BalancesViewModel::class.java)

        _binding = FragmentBalancesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textBalances
        balancesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}