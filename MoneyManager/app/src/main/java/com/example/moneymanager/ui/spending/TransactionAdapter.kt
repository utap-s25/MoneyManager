package com.example.moneymanager.ui.spending

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.database.LocalTransaction as Transaction

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private var totalSpending: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int) =
        if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM

    override fun getItemCount() = transactions.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == VIEW_TYPE_HEADER) {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.total_spending, parent, false)
            HeaderViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaction, parent, false)
            TransactionViewHolder(v)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(totalSpending)
        } else if (holder is TransactionViewHolder) {
            holder.bind(transactions[pos - 1])
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val totalText: TextView = view.findViewById(R.id.month_total)
        private val label: TextView = view.findViewById(R.id.label_total_spending)

        fun bind(totalSpending: String) {
            label.text = "TOTAL SPENDING"
            totalText.text = totalSpending

            // Optionally, add bottom margin to header
            (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                bottomMargin = (16 * itemView.context.resources.displayMetrics.density).toInt()
                itemView.layoutParams = this
            }
        }
    }

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val desc: TextView = view.findViewById(R.id.description)
        private val amt: TextView = view.findViewById(R.id.amount)
        private val date: TextView = view.findViewById(R.id.date)
        private val cat: TextView = view.findViewById(R.id.category)

        fun bind(t: Transaction) {
            desc.text = t.description
            amt.text = "$%.2f".format(t.amount)
            date.text = t.date
            cat.text = t.category
        }
    }
}


