package com.example.moneymanager.repositories

import android.content.ContentValues
import android.content.Context
import com.example.moneymanager.database.BudgetDatabaseHelper
import com.example.moneymanager.database.Category
import com.example.moneymanager.database.Budget
import com.example.moneymanager.database.BudgetDatabaseHelper.Companion.COLUMN_BUDGET_ID
import com.example.moneymanager.database.BudgetDatabaseHelper.Companion.COLUMN_CATEGORY_ID
import com.example.moneymanager.database.BudgetDatabaseHelper.Companion.COLUMN_GUID
import com.example.moneymanager.database.BudgetDatabaseHelper.Companion.COLUMN_NAME
import com.example.moneymanager.database.BudgetDatabaseHelper.Companion.COLUMN_TOTAL
import com.example.moneymanager.database.BudgetDatabaseHelper.Companion.TABLE_BUDGET
import com.example.moneymanager.database.BudgetDatabaseHelper.Companion.TABLE_CATEGORY

class Budget(context: Context) {
    private val dbHelper = BudgetDatabaseHelper(context)

    // Insert a new Category
    fun insertCategory(name: String, guid: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_GUID, guid)
        }
        db.insert(TABLE_CATEGORY, null, values)
        db.close()
    }

    // Insert a new Budget
    fun insertBudget(total: Float, categoryId: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TOTAL, total)
            put(COLUMN_CATEGORY_ID, categoryId)
        }
        db.insert(TABLE_BUDGET, null, values)
        db.close()
    }

    // Get all categories
    fun getAllCategories(): List<Category> {
        val categories = mutableListOf<Category>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_CATEGORY,
            null, null, null, null, null,
            "$COLUMN_NAME ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(BudgetDatabaseHelper.COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val guid = getString(getColumnIndexOrThrow(COLUMN_GUID))
                categories.add(Category(id, name, guid))
            }
            close()
        }
        db.close()
        return categories
    }

    // Get all budgets for a specific category
    fun getBudgetsForCategory(categoryId: Int): List<Budget> {
        val budgets = mutableListOf<Budget>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_BUDGET,
            null,
            "$COLUMN_CATEGORY_ID = ?",
            arrayOf(categoryId.toString()),
            null, null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val budgetId = getInt(getColumnIndexOrThrow(COLUMN_BUDGET_ID))
                val total = getFloat(getColumnIndexOrThrow(COLUMN_TOTAL))
                val categoryId = getInt(getColumnIndexOrThrow(COLUMN_CATEGORY_ID))
                budgets.add(Budget(budgetId, total, categoryId))
            }
            close()
        }
        db.close()
        return budgets
    }

    // Get a specific Budget by its ID
    fun getBudgetById(budgetId: Int): Budget? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_BUDGET,
            null,
            "$COLUMN_BUDGET_ID = ?",
            arrayOf(budgetId.toString()),
            null, null,
            null
        )

        var budget: Budget? = null
        with(cursor) {
            if (moveToFirst()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_BUDGET_ID))
                val total = getFloat(getColumnIndexOrThrow(COLUMN_TOTAL))
                val categoryId = getInt(getColumnIndexOrThrow(COLUMN_CATEGORY_ID))
                budget = Budget(id, total, categoryId)
            }
            close()
        }
        db.close()
        return budget
    }
}
