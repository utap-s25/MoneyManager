package com.example.moneymanager.repositories

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.moneymanager.database.LocalTransaction
import com.example.moneymanager.database.TransactionDatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class Transaction(context: Context) {
    private val dbHelper = TransactionDatabaseHelper(context)

    fun insertTransaction(amount: Double, description: String, date: Long, type: String, guid: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TransactionDatabaseHelper.COLUMN_AMOUNT, amount)
            put(TransactionDatabaseHelper.COLUMN_DESCRIPTION, description)
            put(TransactionDatabaseHelper.COLUMN_DATE, date)
            put(TransactionDatabaseHelper.COLUMN_TYPE, type)
            put(TransactionDatabaseHelper.COLUMN_GUID, guid)
        }
        db.insert(TransactionDatabaseHelper.TABLE_TRANSACTIONS, null, values)
        db.close()
    }

    fun getAllTransactions(): List<LocalTransaction> {
        val transactions = mutableListOf<LocalTransaction>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TransactionDatabaseHelper.TABLE_TRANSACTIONS,
            null, null, null, null, null,
            "${TransactionDatabaseHelper.COLUMN_DATE} DESC"
        )

        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_ID))
                val amount = getDouble(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_AMOUNT))
                val description = getString(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_DESCRIPTION))
                val dateLong = getLong(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_DATE))
                val dateFormatted = dateFormatter.format(Date(dateLong))
                val type = getString(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_TYPE))
                val guid = getString(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_GUID))
                transactions.add(LocalTransaction(id, amount, description, dateFormatted, type, guid))
            }
            close()
        }
        db.close()
        return transactions
    }
}
