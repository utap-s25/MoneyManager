package com.example.moneymanager

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.moneymanager.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.moneymanager.api.TransactionApi
import com.example.moneymanager.api.TransactionHelper
import android.util.Log
import com.example.moneymanager.database.TransactionDatabaseHelper
import com.example.moneymanager.repositories.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_budget, R.id.navigation_spending,
                R.id.navigation_balances, R.id.navigation_messages
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // 🔽 Fetch transactions on app start
        val api = TransactionApi.create()
        val transactionHelper = TransactionHelper()
        val transactionRepo = Transaction(this)


        lifecycleScope.launch {
            try {
                val transactions = transactionHelper.setupAndFetchTestTransactions(api)
                Log.d("MainActivity", "Fetched ${transactions.size} test transactions")

                // Adjusted SimpleDateFormat for the format "yyyy-MM-dd"
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                // Formatter for converting Long back to "MMM dd, yyyy" for debugging
                val displayDateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)

                transactions.forEach { txn ->
                    // Convert the date string to a Long timestamp
                    val parsedDate = try {
                        dateFormatter.parse(txn.date)?.time ?: System.currentTimeMillis()
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Failed to parse date: ${txn.date}", e)
                        System.currentTimeMillis()
                    }

                    // Log the parsed date as Long
                    Log.d("MainActivity", "Parsed date as Long: $parsedDate")

                    // Convert Long back to "MMM dd, yyyy" for debugging
                    val formattedDate = displayDateFormatter.format(parsedDate)
                    Log.d("MainActivity", "Converted back to formatted date: $formattedDate")

                    // Insert the transaction with the parsed date as Long
                    transactionRepo.insertTransaction(
                        amount = txn.amount,
                        description = txn.description,
                        date = parsedDate,
                        type = txn.category,
                        guid = txn.guid
                    )
                }

                Log.d("MainActivity", "Inserted transactions into local DB")
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to fetch or insert transactions", e)
            }
        }
    }
}