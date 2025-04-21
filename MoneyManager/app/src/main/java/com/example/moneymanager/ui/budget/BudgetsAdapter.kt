package com.example.moneymanager.ui.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.database.LocalBudget

class BudgetsAdapter(private val budgets: List<LocalBudget>) :
    RecyclerView.Adapter<BudgetsAdapter.BudgetViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]
        holder.bind(budget)
    }

    override fun getItemCount(): Int = budgets.size

    class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.budget_name)
        private val amountTextView: TextView = itemView.findViewById(R.id.budget_amount)
        private val percentTextView: TextView = itemView.findViewById(R.id.budget_percent)

        fun bind(budget: LocalBudget) {
            nameTextView.text = budget.name
            amountTextView.text = "$${budget.amount}"
            percentTextView.text = "${budget.percentSpent}%"
        }
    }
}
