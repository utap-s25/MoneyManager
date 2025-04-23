package com.example.moneymanager.ui.budget

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.moneymanager.Constants
import com.example.moneymanager.api.BudgetsApi
import com.example.moneymanager.databinding.FragmentCreateBudgetBinding
import com.example.moneymanager.repositories.Budget
import kotlinx.coroutines.launch

class CreateBudgetFragment : Fragment() {

    private var _binding: FragmentCreateBudgetBinding? = null
    private val binding get() = _binding!!

    private val categoryList = listOf(
        "Business Services",
        "Utilities",
        "Education",
        "Transportation"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBudgetBinding.inflate(inflater, container, false)

        // Setup Spinner with dummy categories
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categoryList
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = spinnerAdapter

        // "Create Budget" button logic
        binding.submitBudgetButton.setOnClickListener {
            val selectedCategory = binding.categorySpinner.selectedItem.toString()
            val amountText = binding.budgetAmountEditText.text.toString()

            // Validate input
            if (selectedCategory.isNotEmpty() && amountText.isNotEmpty()) {
                val amount = amountText.toDoubleOrNull()

                if (amount != null) {
                    // --- Save this logic for later ---
                    lifecycleScope.launch {
                        try {
                            val api = BudgetsApi.create()

                            // Assume category GUID mapping is handled based on selectedCategory
                            val categoryGuid = when (selectedCategory) {
                                "Business Services" -> "CAT-94b11142-e97b-941a-f67f-6e18d246a23f"
                                "Utilities" -> "CAT-79b02f2f-2adc-88f0-ac2b-4e71ead9cfc8"
                                "Education" -> "CAT-bf5c9cca-c96b-b50d-440d-38d9adfda5b0"
                                "Transportation" -> "CAT-7829f71c-2e8c-afa5-2f55-fa3634b89874"
                                else -> ""
                            }

                            // Proceed to create the budget using API
                            val prefs = requireContext().getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE)
                            val mxUserGuid = prefs.getString(Constants.MX_USER_GUID_KEY, null) ?:
                                throw IllegalStateException("MX user GUID is missing. Cannot proceed.")
                            val response = api.createNewBudget(
                                userGuid = mxUserGuid, // Replace with dynamic user GUID
                                body = BudgetsApi.CreateBudgetWrapper(
                                    budget = BudgetsApi.CreateBudgetBody(
                                        amount = amount,
                                        category_guid = categoryGuid
                                    )
                                )
                            )

                            val createdBudget = response.budget
                            Log.d("CreateBudgetFragment", "Successfully created budget: $createdBudget")

                            // Insert into local DB (after API call)
                            val budgetRepo = Budget(requireContext())
                            budgetRepo.insertBudget(
                                guid = createdBudget.guid,
                                name = createdBudget.name,
                                percent = createdBudget.percent_spent,
                                amount = createdBudget.amount,
                                categoryId = createdBudget.category_guid
                            )

                            Log.d("CreateBudgetFragment", "Inserted new budget into local DB")

                            // Go back to the previous fragment
                            findNavController().popBackStack()

                        } catch (e: Exception) {
                            Log.e("CreateBudgetFragment", "Failed to create or save budget", e)
                        }
                    }
                } else {
                    // Invalid amount input
                    Log.e("CreateBudgetFragment", "Invalid budget amount")
                }
            } else {
                // Handle empty inputs
                Log.e("CreateBudgetFragment", "Category or amount is empty")
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
