package com.example.moneymanager.repositories

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.moneymanager.database.LocalBudget
import com.example.moneymanager.database.DatabaseHelper

class Budget(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertBudget(
        guid: String,
        name: String,
        amount: Double,
        percent: Double,
        categoryId: String
    ) {
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM ${DatabaseHelper.TABLE_CATEGORY} WHERE ${DatabaseHelper.COLUMN_GUID} = ?",
            arrayOf(categoryId)
        )
        if (cursor.moveToFirst()) {
            Log.d("insertBudget", "Category found for categoryId: $categoryId")

            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_GUID, guid)
                put(DatabaseHelper.COLUMN_NAME, name)
                put(DatabaseHelper.COLUMN_AMOUNT, amount)
                put(DatabaseHelper.COLUMN_PERCENT, percent)
                put(DatabaseHelper.COLUMN_CATEGORY_ID, categoryId)
            }
            db.insertWithOnConflict(
                DatabaseHelper.TABLE_BUDGETS,
                null,
                values,
                android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
            )
            Log.d("insertBudget", "Inserted budget for categoryId: $categoryId")
        } else {
            Log.e("insertBudget", "Category ID not found, skipping insert: $categoryId")
        }

        cursor.close()
        db.close()
    }

    fun getAllBudgets(): List<LocalBudget> {
        val budgets = mutableListOf<LocalBudget>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_BUDGETS,
            null, null, null, null, null, null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val guid = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_GUID))
                val name = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME))
                val amount = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
                val percent = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PERCENT))
                val categoryId = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID))
                budgets.add(LocalBudget(id, guid, name, percent, amount, categoryId))
            }
            close()
        }
        db.close()
        return budgets
    }

    fun deleteBudgetByGuid(guid: String) {
        val db = dbHelper.readableDatabase
        db.delete("budgets", "guid = ?", arrayOf(guid))
    }
}
