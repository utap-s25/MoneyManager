package com.example.moneymanager.ui.spending

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentSpendingBinding
import com.example.moneymanager.repositories.Transaction as TransactionRepo
import java.text.SimpleDateFormat
import java.util.*

class SpendingFragment : Fragment() {

    private var _binding: FragmentSpendingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val spendingViewModel = ViewModelProvider(this).get(SpendingViewModel::class.java)
        _binding = FragmentSpendingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 1. Get all transactions
        val transactionRepo = TransactionRepo(requireContext())
        val allTransactions = transactionRepo.getAllTransactions()

        // 2. Function to filter and update adapter based on selected month
        fun updateRecyclerForMonth(selectedMonth: String) {
            val filtered = if (selectedMonth.isEmpty()) {
                allTransactions // Show all if no month is selected
            } else {
                allTransactions.filter {
                    // Format the transaction date to get the month abbreviation (e.g., "Apr")
                    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                    Log.d("SpendingFragment", "Checking transaction date: ${it.date}")
                    val date = formatter.parse(it.date) // Assuming `it.date` is a string like "Apr 15, 2025"
                    val monthAbbr = SimpleDateFormat("MMM", Locale.US).format(date)
                    Log.d("SpendingFragment", "Checking transaction date: ${it.date}, Parsed month: $monthAbbr")

                    // Compare the extracted month abbreviation with the selected one
                    monthAbbr == selectedMonth
                }
            }
            val total = filtered.sumOf { it.amount }
            Log.d("SpendingFragment", "$total")
            spendingViewModel.updateTotalSpending(total.toFloat())

            val adapter = TransactionAdapter(filtered, "$%.2f".format(total))
            binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.transactionsRecyclerView.adapter = adapter
        }

        // 3. Set up the spinner for month selection
        val spinner = root.findViewById<Spinner>(R.id.month_spinner)
        val months = resources.getStringArray(R.array.months_array) // Full month names (e.g., January, February)
        val abbrMonths = resources.getStringArray(R.array.months_abbr_array) // Abbreviations (e.g., Jan, Feb)

        // Determine the current month and set it as default
        val currentMonthAbbr = SimpleDateFormat("MMM", Locale.US).format(Date())
        val currentMonthPosition = abbrMonths.indexOf(currentMonthAbbr)

        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, months
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = adapter
        updateRecyclerForMonth(currentMonthAbbr)
        // Set the spinner's selected item to the current month
        spinner.setSelection(currentMonthPosition)

        // Set up listener for spinner selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, pos: Int, id: Long
            ) {
                val selectedMonth = abbrMonths.getOrNull(pos) ?: abbrMonths[0]
                Log.d("SpendingFragment", "Selected month: $selectedMonth")  // Added log statement
                // Update RecyclerView with the selected month abbreviation
                updateRecyclerForMonth(selectedMonth)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
