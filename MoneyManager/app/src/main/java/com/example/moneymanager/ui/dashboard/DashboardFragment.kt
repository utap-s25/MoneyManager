package com.example.moneymanager.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentDashboardBinding
import com.example.moneymanager.ui.balances.BalancesViewModel
import com.example.moneymanager.ui.budget.BudgetViewModel
import com.example.moneymanager.ui.messages.MessagesViewModel
import com.example.moneymanager.ui.spending.SpendingViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.moneymanager.repositories.Accounts as AccountRepo
import com.example.moneymanager.repositories.Budget as BudgetRepo
import com.example.moneymanager.repositories.Transaction as TransactionRepo

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val TAG = "DashboardFragment"
    private val decimalFormat: DecimalFormat = DecimalFormat.getInstance(Locale.getDefault()) as DecimalFormat

    init {
        decimalFormat.applyPattern("#,##0.00");
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Update textDashboard via the ViewModel
        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) { text ->
            textView.text = text
            Log.d(TAG, "ViewModel text updated: $text")
        }

        // Add dynamic widgets
        addDashboardWidgets()

        return root
    }

    private fun addDashboardWidgets() {
        val widgets = listOf("Overview", "Spending", "Balances", "Messages")
        val container = binding.widgetContainer

        if (container == null) {
            Log.e(TAG, "widgetContainer not found in layout!")
            return
        }

        // Remove any existing views just in case
        container.removeAllViews()

        widgets.forEach { widgetName ->
            // Inflate the widget layout (ensure fragment_dashboard_widget.xml exists and has a TextView with id widget_title)
            Log.d("WidgetTag", "In the widget list")
            val widgetView = layoutInflater.inflate(R.layout.fragment_dashboard_widget, container, false)
            val titleTextView = widgetView.findViewById<TextView>(R.id.widget_title)
            titleTextView?.text = widgetName
            if (widgetName == "Overview") {
                titleTextView?.gravity = Gravity.CENTER
                createOverViewWidget(widgetView)
            } else if (widgetName == "Spending") {
                createSpendingWidget(widgetView)
            } else if (widgetName == "Balances") {
                createBalancesWidget(widgetView)
            } else if (widgetName == "Messages") {
                createMessagesWidget(widgetView)
            }
            container.addView(widgetView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun formatAmount(amount: Float): String {
        return when {
            amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
            else -> amount.toString()
        }
    }

    private fun createOverViewWidget(widgetView: View) {
        val chartContainer = widgetView.findViewById<LinearLayout>(R.id.widget_chart_container)
        chartContainer.visibility = View.VISIBLE
        val contentContainer = widgetView.findViewById<TextView>(R.id.widget_content)
        contentContainer.visibility = View.GONE
        val titleContainer = widgetView.findViewById<TextView>(R.id.widget_title)
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val currentMonth = monthFormat.format(Date())
        titleContainer.text = "$currentMonth Overview"

        val budgetRepo = BudgetRepo(requireContext())
        val localBudgets = budgetRepo.getAllBudgets()

        val colors = listOf(
            R.color.blue,
            R.color.purple,
            R.color.orange,
            R.color.gold
        )

        val categories = localBudgets.mapIndexed { index, it ->
            val spent = (it.amount * it.percentSpent / 100).toFloat()
            val left = (it.amount - spent).toFloat()
            val colorRes = colors[index % colors.size]
            BudgetCategory(it.name, spent, left, colorRes)
        }

        categories.forEach { category ->
            val total = category.spent + category.left
            val spentWeight = category.spent / total
            val leftWeight = category.left / total

            val barLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    60
                ).apply {
                    topMargin = 4
                }
            }

            val spentView = FrameLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, spentWeight)
                setBackgroundResource(category.colorRes)

                if (spentWeight >= 0.2f) {
                    addView(TextView(requireContext()).apply {
                        text = "$${formatAmount(category.spent)}"
                        setTextColor(resources.getColor(android.R.color.white, null))
                        textSize = 16f
                        setPadding(12, 0, 0, 0)
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        ).apply {
                            gravity = android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL
                        }
                    })
                }
            }

            val remainingView = FrameLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, leftWeight)
                setBackgroundResource(R.color.gray)

                addView(TextView(requireContext()).apply {
                    text = "$${formatAmount(category.left)}"
                    setTextColor(resources.getColor(android.R.color.black, null))
                    textSize = 16f
                    setPadding(0, 0, 12, 0)
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                        gravity = android.view.Gravity.END or android.view.Gravity.CENTER_VERTICAL
                    }
                })
            }


            // Add category name as a label ABOVE the bar
            val label = TextView(requireContext()).apply {
                text = category.name
                textSize = 16f
                setPadding(1, 0, 0, 1)
            }

            widgetView.setOnClickListener {
                // Navigate to the Budget screen
                findNavController().popBackStack()
                findNavController().navigate(R.id.navigation_budget)
            }

            chartContainer.addView(label)
            barLayout.addView(spentView)
            barLayout.addView(remainingView)
            chartContainer.addView(barLayout)
        }
    }

    // Function to handle the "Spending" widget
    private fun createSpendingWidget(widgetView: View) {
        val contentContainer = widgetView.findViewById<TextView>(R.id.widget_content)
        val spendingViewModel = ViewModelProvider(requireActivity())[SpendingViewModel::class.java]
        val onCreateTotalSpending = spendingViewModel.totalSpending.value
        contentContainer.text = "$onCreateTotalSpending"

        spendingViewModel.totalSpending.observe(viewLifecycleOwner, Observer { totalSpending ->
            // Update the TextView with the current remaining budget value
            contentContainer.text = "$${decimalFormat.format(totalSpending)}"
            contentContainer.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        })

        val transactionRepo = TransactionRepo(requireContext())
        spendingViewModel.updateTotalSpending(transactionRepo.getCurrentMonthlySpending())

        widgetView.setOnClickListener {
            // Navigate to the Spending screen
            findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_spending)
        }
    }

    // Function to handle the "Balances" widget
    private fun createBalancesWidget(widgetView: View) {
        val contentContainer = widgetView.findViewById<TextView>(R.id.widget_content)
        val balancesViewModel = ViewModelProvider(requireActivity())[BalancesViewModel::class.java]
        val onCreateTotalBalance = balancesViewModel.totalBalance.value
        contentContainer.text = "$onCreateTotalBalance"
        balancesViewModel.totalBalance.observe(viewLifecycleOwner, Observer { totalBalance ->
            // Update the TextView with the current remaining budget value
            contentContainer.text = "$${decimalFormat.format(totalBalance)}"
            if (totalBalance > 0) {
                contentContainer.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            } else {
                contentContainer.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            }
        })

        val accountRepo = AccountRepo(requireContext())
        balancesViewModel.setTotalBalance(accountRepo.getAccountsTotal())

        widgetView.setOnClickListener {
            // Navigate to the Balances screen
            findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_balances)
        }
    }

    // Function to handle the "Messages" widget
    private fun createMessagesWidget(widgetView: View) {
        val contentContainer = widgetView.findViewById<TextView>(R.id.widget_content)
        val messagesViewModel = ViewModelProvider(requireActivity())[MessagesViewModel::class.java]

        messagesViewModel.newMessages.observe(viewLifecycleOwner, Observer { newMessages ->
            // Update the TextView with the current remaining budget value
            Log.d("createMessagesWidget", " THIS SHOULD BE UPDATING: $newMessages")
            "$newMessages NEW".also { contentContainer.text = it }
        })

        widgetView.setOnClickListener {
            // Navigate to the Messages screen
            findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_messages)
        }
    }


}

data class BudgetCategory(
    val name: String,
    val spent: Float,
    val left: Float,
    val colorRes: Int
)