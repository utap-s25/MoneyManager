package com.example.moneymanager.ui.budget

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.api.BudgetsApi
import com.example.moneymanager.database.LocalBudget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BudgetsAdapter(
    private val budgets: MutableList<LocalBudget>,
    private val onDelete: (String, Int) -> Unit
) : RecyclerView.Adapter<BudgetsAdapter.BudgetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]
        holder.bind(budget)
    }

    override fun getItemCount(): Int = budgets.size

    fun deleteBudgetAtPosition(position: Int) {
        budgets.removeAt(position)  // Remove the item from the list
        notifyItemRemoved(position)  // Notify the adapter that the item has been removed
    }

    inner class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.budget_name)
        private val amountTextView: TextView = itemView.findViewById(R.id.budget_amount)
        private val spentView: View = itemView.findViewById(R.id.spent_view)
        private val remainingView: View = itemView.findViewById(R.id.remaining_view)
        private val spentAmountText: TextView = itemView.findViewById(R.id.spent_amount_text)
        private val closeIcon: View = itemView.findViewById(R.id.close_icon)

        fun bind(budget: LocalBudget) {
            nameTextView.text = budget.name
            amountTextView.text = "$${budget.amount}"

            // Use ViewTreeObserver to ensure that the layout is measured before calculating the width

            closeIcon.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // Immediately remove the item from the adapter and update UI
                    onDelete(budget.guid, currentPosition)

                    // Then attempt the API call, but don't block the UI on it
                    val budgetsApi = BudgetsApi.create()
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            budgetsApi.deleteBudget("USR-9cd24e37-15f6-4938-958a-7f0798e63c3c", budget.guid)
                            Log.d("BudgetsAdapter", "Successfully deleted budget ${budget.guid}")
                        } catch (e: Exception) {
                            Log.e("BudgetsAdapter", "Failed to delete budget ${budget.guid}", e)
                            // Optionally show a toast/snackbar on the main thread
                            withContext(Dispatchers.Main) {
                                // e.g., Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            itemView.viewTreeObserver.addOnPreDrawListener {
                val totalWidth = itemView.width // This will now be correct after the layout is measured

                val percentSpent = budget.percentSpent
                val spentWidth = (totalWidth * (percentSpent / 100)).toInt()
                val remainingWidth = totalWidth - spentWidth

                // Set the width of the spent view
                val paramsSpent = spentView.layoutParams
                paramsSpent.width = spentWidth
                spentView.layoutParams = paramsSpent

                // Set the width of the remaining view
                val paramsRemaining = remainingView.layoutParams
                paramsRemaining.width = remainingWidth
                remainingView.layoutParams = paramsRemaining

                // Calculate amount spent
                val spentAmount = (budget.amount * (percentSpent / 100))

                // Set the text of the spent amount
                spentAmountText.text = "Spent: $${spentAmount}"

                // Optionally change the color of the spent view (green/red depending on the remaining amount)
                if (spentAmount == budget.amount) {
                    spentView.setBackgroundResource(android.R.color.holo_red_dark) // Red if all is spent
                    amountTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
                } else {
                    spentView.setBackgroundResource(android.R.color.holo_green_light) // Green otherwise

                }




                true
            }

        }



//        fun deleteBudgetItem(budgetGuid: String, position: Int) {
//            val userGuid = "USR-9cd24e37-15f6-4938-958a-7f0798e63c3c" // Replace this with actual user GUID
//
//            val budgetsApi = BudgetsApi.create()
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    budgetsApi.deleteBudget(userGuid, budgetGuid)
//                    withContext(Dispatchers.Main) {
//                        onDeleteConfirmed(position) // 👈 Trigger deletion in Fragment
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        // Show error message if needed
//                    }
//                }
//            }
//        }


    }
}
