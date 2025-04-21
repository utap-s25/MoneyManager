package com.example.moneymanager.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentDashboardBinding
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.Observer
import androidx.lifecycle.LiveData
import com.example.moneymanager.ui.balances.BalancesViewModel
import com.example.moneymanager.ui.budget.BudgetViewModel
import com.example.moneymanager.ui.messages.MessagesViewModel
import com.example.moneymanager.ui.spending.SpendingViewModel
import com.example.moneymanager.ui.spending.SpendingCategory
import java.text.SimpleDateFormat
import java.util.*
import java.text.DecimalFormat
import com.example.moneymanager.repositories.Transaction as TransactionRepo

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val TAG = "DashboardFragment"
    private val decimalFormat: DecimalFormat = DecimalFormat.getInstance(Locale.getDefault()) as DecimalFormat

    init {
        decimalFormat.applyPattern("#,###.00")
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

        //createMockData()

        // Add dynamic widgets
        addDashboardWidgets()

        return root
    }

    private fun addDashboardWidgets() {
        val widgets = listOf("Overview", "Budget", "Spending", "Balances", "Messages")
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
            } else if (widgetName == "Budget") {
                createBudgetWidget(widgetView)
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

    private fun createOverViewWidget(widgetView: View) {
        val chartContainer = widgetView.findViewById<LinearLayout>(R.id.widget_chart_container)
        chartContainer.visibility = View.VISIBLE
        val contentContainer = widgetView.findViewById<TextView>(R.id.widget_content)
        contentContainer.visibility = View.GONE
        val titleContainer = widgetView.findViewById<TextView>(R.id.widget_title)
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val currentMonth = monthFormat.format(Date())
        titleContainer.text = "$currentMonth Overview"

        val categories = listOf(
            BudgetCategory("Groceries", 300f, 200f, R.color.blue),
            BudgetCategory("Entertainment", 150f, 350f, R.color.purple),
            BudgetCategory("Transportation", 250f, 150f, R.color.orange),
            BudgetCategory("Housing", 800f, 200f, R.color.green),
            BudgetCategory("Miscellaneous", 500f, 100f, R.color.yellow)
        )

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

                addView(TextView(requireContext()).apply {
                    text = "$${category.spent.toInt()}"
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

            val remainingView = FrameLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, leftWeight)
                setBackgroundResource(R.color.gray)

                addView(TextView(requireContext()).apply {
                    text = "$${category.left.toInt()}"
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

            chartContainer.addView(label)
            barLayout.addView(spentView)
            barLayout.addView(remainingView)
            chartContainer.addView(barLayout)
        }
    }

    private fun createBudgetWidget(widgetView: View) {
        val contentContainer = widgetView.findViewById<TextView>(R.id.widget_content)
        val budgetViewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)

        val onCreateRemainingBudget = budgetViewModel.remainingBudget.value
        contentContainer.text = "$onCreateRemainingBudget"
        budgetViewModel.remainingBudget.observe(viewLifecycleOwner, Observer { remainingBudget ->
            // Update the TextView with the current remaining budget value
            contentContainer.text = "$${decimalFormat.format(remainingBudget)}"
            if (remainingBudget > 0) {
                contentContainer.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            } else {
                contentContainer.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            }
        })

        widgetView.setOnClickListener {
            // Navigate to the Budget screen
            findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_budget)
        }
    }

    // Function to handle the "Spending" widget
    private fun createSpendingWidget(widgetView: View) {
        val contentContainer = widgetView.findViewById<TextView>(R.id.widget_content)
        val spendingViewModel = ViewModelProvider(this).get(SpendingViewModel::class.java)
        val onCreateTotalSpending = spendingViewModel.totalSpending.value
        contentContainer.text = "$onCreateTotalSpending"

        spendingViewModel.totalSpending.observe(viewLifecycleOwner, Observer { totalSpending ->
            // Update the TextView with the current remaining budget value
            contentContainer.text = "$${decimalFormat.format(totalSpending)}"
            contentContainer.setTextColor(android.graphics.Color.RED)
        })

        val transactionRepo = TransactionRepo(requireContext())

        widgetView.setOnClickListener {
            // Navigate to the Spending screen
            findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_spending)
        }
    }

    // Function to handle the "Balances" widget
    private fun createBalancesWidget(widgetView: View) {
        val contentContainer = widgetView.findViewById<TextView>(R.id.widget_content)
        val balancesViewModel = ViewModelProvider(this).get(BalancesViewModel::class.java)

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

        widgetView.setOnClickListener {
            // Navigate to the Balances screen
            findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_balances)
        }
    }

    // Function to handle the "Messages" widget
    private fun createMessagesWidget(widgetView: View) {
        val contentContainer = widgetView.findViewById<TextView>(R.id.widget_content)
        val messagesViewModel = ViewModelProvider(this).get(MessagesViewModel::class.java)

        val onCreateNewMessages = messagesViewModel.newMessages.value
        contentContainer.text = "$onCreateNewMessages"
        messagesViewModel.newMessages.observe(viewLifecycleOwner, Observer { newMessages ->
            // Update the TextView with the current remaining budget value
            contentContainer.text = "$newMessages NEW"
        })

        widgetView.setOnClickListener {
            // Navigate to the Messages screen
            findNavController().popBackStack()
            findNavController().navigate(R.id.navigation_messages)
        }
    }

//    private fun createMockData() {
//        val budgetViewModel = ViewModelProvider(this).get(BudgetViewModel::class.java)
//        // TEST DATA REMOVE
//        budgetViewModel.setTotalBudget(10000f)
//        // TEST DATA REMOVE
//        budgetViewModel.updateSpending(1800f)
//
//        val spendingViewModel = ViewModelProvider(this).get(SpendingViewModel::class.java)
//
//// List of categories
//        val categories = listOf(
//            SpendingCategory("Groceries", 300f),
//            SpendingCategory("Entertainment", 150f),
//            SpendingCategory("Transportation", 250f),
//            SpendingCategory("Housing", 800f),
//            SpendingCategory("Dining Out", 120f),
//            SpendingCategory("Miscellaneous", 50f)
//        )
//
//// Update the spending categories in the ViewModel
//        spendingViewModel.updateSpendingCategories(categories)
//
//// Observe the LiveData
//        spendingViewModel.totalSpending.observe(viewLifecycleOwner) { total ->
//            println("Total Spending: $total")
//        }
//
//        spendingViewModel.topSpendingCategories.observe(viewLifecycleOwner) { topCategories ->
//            println("Top 4 Spending Categories: $topCategories")
//        }
//
//        spendingViewModel.miscellaneousSpending.observe(viewLifecycleOwner) { miscellaneous ->
//            println("Miscellaneous Spending: $miscellaneous")
//        }
//
//        val balancesViewModel = ViewModelProvider(this).get(BalancesViewModel::class.java)
//        balancesViewModel.setTotalBalance(10000000f)
//
//        val messagesViewModel = ViewModelProvider(this).get(MessagesViewModel::class.java)
//        messagesViewModel.setNewMessages(3)
//    }
}

data class BudgetCategory(
    val name: String,
    val spent: Float,
    val left: Float,
    val colorRes: Int
)