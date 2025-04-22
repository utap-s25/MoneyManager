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
import com.example.moneymanager.api.AccountApi
import com.example.moneymanager.repositories.Transaction
import com.example.moneymanager.repositories.Accounts
import com.example.moneymanager.api.AccountHelper
import com.example.moneymanager.api.BudgetsApi
import com.example.moneymanager.api.BudgetsHelper
import com.example.moneymanager.repositories.Budget
import java.text.SimpleDateFormat
import java.util.Locale
import android.view.Menu
import android.view.MenuItem
import com.example.moneymanager.ui.messages.MessagesViewModel
import androidx.lifecycle.ViewModelProvider

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

        var receiverId = "SWV6Vp1NeAVoAgKBjC1cM2iD9E13"
        if (currentUser!!.uid == receiverId) {
            receiverId = "ODNZCYCuUyTDLXQVeeOZZuMhg2E2"
        }

        val messagesViewModel = ViewModelProvider(this).get(MessagesViewModel::class.java)
        messagesViewModel.startListeningForMessages(currentUser.uid, receiverId)
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

        // 🔽 Fetch transactions and accounts on app start
        val api = TransactionApi.create()
        val accountApi = AccountApi.create()
        val accountHelper = AccountHelper()
        val transactionHelper = TransactionHelper()
        val transactionRepo = Transaction(this)
        val accountsRepo = Accounts(this)
        val budgetHelper = BudgetsHelper()
        val budgetRepo = Budget(this)
        val budgetApi = BudgetsApi.create()

        lifecycleScope.launch {
            try {
                // Fetch transactions
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

                // Fetch accounts
                val accounts = accountHelper.setupAndFetchAccounts(accountApi, "USR-9cd24e37-15f6-4938-958a-7f0798e63c3c") // Fetch accounts from the API
                Log.d("MainActivity", "Fetched ${accounts.size} accounts")

                // Insert each account into the local database
                accounts.forEach { account ->
                    accountsRepo.insertAccount(
                        type = account.type,
                        balance = account.balance,
                        name = account.name,
                        guid = account.guid
                    )
                }



                val budget = budgetHelper.setupAndFetchBudgets(budgetApi, "USR-9cd24e37-15f6-4938-958a-7f0798e63c3c")
                Log.d("MainActivity", "Fetched ${budget.size} budgets")

                budget.forEach { budget ->
                    Log.d("mainActivity", "${budget.categoryId}")
                    budgetRepo.insertBudget(
                        guid = budget.guid,
                        name = budget.name,
                        percent = budget.percentSpent,
                        amount = budget.amount,
                        categoryId = budget.categoryId
                    )
                }

                val allBudgets = budgetRepo.getAllBudgets()
                Log.d("mainActivity", "Budgets in DB:\n" + allBudgets.joinToString("\n") { it.toString() })


                Log.d("MainActivity", "Inserted accounts into local DB")
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to fetch or insert data", e)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_account -> {
                val navController = findNavController(R.id.nav_host_fragment_activity_main)
                navController.navigate(R.id.navigation_account)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
