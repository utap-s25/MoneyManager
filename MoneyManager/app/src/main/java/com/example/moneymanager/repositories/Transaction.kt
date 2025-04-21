package com.example.moneymanager.repositories

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.moneymanager.database.LocalTransaction
import com.example.moneymanager.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class Transaction(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertTransaction(amount: Double, description: String, date: Long, type: String, guid: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_AMOUNT, amount)
            put(DatabaseHelper.COLUMN_DESCRIPTION, description)
            put(DatabaseHelper.COLUMN_DATE, date)
            put(DatabaseHelper.COLUMN_TYPE, type)
            put(DatabaseHelper.COLUMN_GUID, guid)
        }
        db.insert(DatabaseHelper.TABLE_TRANSACTIONS, null, values)
        db.close()
    }

    fun getAllTransactions(): List<LocalTransaction> {
        val transactions = mutableListOf<LocalTransaction>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TRANSACTIONS,
            null, null, null, null, null,
            "${DatabaseHelper.COLUMN_DATE} DESC"
        )

        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val amount = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT))
                val description = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
                val dateLong = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE))
                val dateFormatted = dateFormatter.format(Date(dateLong))
                val type = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE))
                val guid = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_GUID))
                transactions.add(LocalTransaction(id, amount, description, dateFormatted, type, guid))
            }
            close()
        }
        db.close()
        return transactions
    }

    fun getMonthlySpending(month: Int, year: Int): Float {
        val db = dbHelper.readableDatabase
        Log.d("getMonthlySpending", "Month: $month, Year: $year")

        // Start of the given month
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val monthStart = calendar.timeInMillis

        // Start of the next month
        calendar.add(Calendar.MONTH, 1)
        val monthEnd = calendar.timeInMillis
        Log.d("getMonthlySpending", "Date range: $monthStart to $monthEnd")

        val cursor = db.rawQuery(
            """
        SELECT SUM(${DatabaseHelper.COLUMN_AMOUNT})
        FROM ${DatabaseHelper.TABLE_TRANSACTIONS}
        WHERE ${DatabaseHelper.COLUMN_DATE} >= ?
        AND ${DatabaseHelper.COLUMN_DATE} < ?
        """.trimIndent(),
            arrayOf(monthStart.toString(), monthEnd.toString())
        )

        var total = 0f
        if (cursor.moveToFirst()) {
            total = cursor.getFloat(0)
            Log.d("getMonthlySpending", "Total spending found: $total")
        } else {
            Log.d("getMonthlySpending", "No results found.")
        }

        cursor.close()
        db.close()

        return total
    }

    fun getCurrentMonthlySpending(): Float {
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear = now.get(Calendar.YEAR)
        return getMonthlySpending(currentMonth, currentYear)
    }
}
