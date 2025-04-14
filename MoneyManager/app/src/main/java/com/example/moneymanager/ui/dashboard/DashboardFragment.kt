package com.example.moneymanager.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanager.R
import com.example.moneymanager.databinding.FragmentDashboardBinding
import androidx.navigation.fragment.findNavController

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val TAG = "DashboardFragment"

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
        val widgets = listOf("Budget", "Spending", "Balances", "Messages")
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
            widgetView.findViewById<TextView>(R.id.widget_title)?.text = widgetName

            // Optional: set a click listener to navigate to detailed view
            widgetView.setOnClickListener {
                val destinationId = when (widgetName) {
                    "Budget" -> R.id.navigation_budget
                    "Spending" -> R.id.navigation_spending
                    "Balances" -> R.id.navigation_balances
                    "Messages" -> R.id.navigation_messages
                    else -> null
                }
                destinationId?.let {
                    // Navigate to the destination defined in your mobile_navigation.xml
                    findNavController().popBackStack()
                    findNavController().navigate(it)
                }
            }

            container.addView(widgetView)
            Log.d(TAG, "Added widget: $widgetName")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
