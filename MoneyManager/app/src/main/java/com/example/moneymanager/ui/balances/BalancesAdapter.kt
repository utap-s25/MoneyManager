package com.example.moneymanager.ui.balances

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanager.R
import com.example.moneymanager.database.LocalAccount

class AccountBalanceAdapter(private val accountList: List<LocalAccount>) :
    RecyclerView.Adapter<AccountBalanceAdapter.AccountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_account_balance, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accountList[position]
        holder.bind(account)
    }

    override fun getItemCount(): Int = accountList.size

    class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val balanceTextView: TextView = itemView.findViewById(R.id.accountAmountTextView)
        private val typeTextView: TextView = itemView.findViewById(R.id.accountTypeTextView)
        private val nameTextView: TextView = itemView.findViewById(R.id.accountNameTextView)


        @SuppressLint("DefaultLocale")
        fun bind(account: LocalAccount) {
            val balance = account.balance
            if (balance < 0) {
                balanceTextView.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            } else {
                balanceTextView.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            }

            val formattedBalance = "$${String.format("%.2f", kotlin.math.abs(balance))}"
            balanceTextView.text = formattedBalance
            typeTextView.text = account.type
            nameTextView.text = account.name
        }
    }
}
