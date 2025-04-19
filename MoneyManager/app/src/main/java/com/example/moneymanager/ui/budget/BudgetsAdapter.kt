package com.example.moneymanager.ui.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.api.BudgetsApi.Budget
import com.example.moneymanager.ui.spending.TransactionAdapter
import com.example.moneymanager.ui.spending.TransactionAdapter.Companion
import com.example.moneymanager.ui.spending.TransactionAdapter.HeaderViewHolder
import com.example.moneymanager.ui.spending.TransactionAdapter.TransactionViewHolder

class BudgetsAdapter(private val budgets: List<Budget>, private val totalAmount: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int) =
        if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM

    override fun getItemCount() = budgets.size + 1  // +1 for the header

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == VIEW_TYPE_HEADER) {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_budgets_total, parent, false)
            HeaderViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_budget, parent, false)
            BudgetViewHolder(v)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(totalAmount)
        } else if (holder is BudgetViewHolder) {
            println("Budget HERE --- Name: ${budgets[position - 1].name}")
            holder.bind(budgets[position - 1])  // -1 because header occupies position 0
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val totalText: TextView = view.findViewById(R.id.budgets_total)
        private val label: TextView = view.findViewById(R.id.budgets_label)

        fun bind(totalAmount: String) {
            label.text = "TOTAL BUDGET"
            totalText.text = totalAmount

            // Optionally, add bottom margin to header for better spacing
            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                bottomMargin = (16 * itemView.context.resources.displayMetrics.density).toInt()
                itemView.layoutParams = this
            }
        }
    }

    class BudgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.budget_name)
        private val amount: TextView = view.findViewById(R.id.budget_amount)
        private val percent: TextView = view.findViewById(R.id.budget_percent)

        fun bind(budget: Budget) {
            val nameStr = budget.name
            println("BUDGET: $nameStr")
            name.text = budget.name
            amount.text = "$${budget.amount}"
            percent.text = "${budget.percent_spent}%"
        }
    }
}