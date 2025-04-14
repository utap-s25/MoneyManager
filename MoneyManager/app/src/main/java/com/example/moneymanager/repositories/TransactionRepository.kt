package com.example.moneymanager.repositories

import android.content.ContentValues
import android.content.Context
import com.example.moneymanager.database.Transaction
import com.example.moneymanager.database.TransactionDatabaseHelper

public class TransactionRepository(context: Context) {
    private val dbHelper = TransactionDatabaseHelper(context)

    fun insertTransaction(amount: Double, description: String, date: Long, type: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(TransactionDatabaseHelper.COLUMN_AMOUNT, amount)
            put(TransactionDatabaseHelper.COLUMN_DESCRIPTION, description)
            put(TransactionDatabaseHelper.COLUMN_DATE, date)
            put(TransactionDatabaseHelper.COLUMN_TYPE, type)
        }
        db.insert(TransactionDatabaseHelper.TABLE_TRANSACTIONS, null, values)
        db.close()
    }

    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TransactionDatabaseHelper.TABLE_TRANSACTIONS,
            null, null, null, null, null,
            "${TransactionDatabaseHelper.COLUMN_DATE} DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_ID))
                val amount = getDouble(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_AMOUNT))
                val description = getString(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_DESCRIPTION))
                val date = getLong(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_DATE))
                val type = getString(getColumnIndexOrThrow(TransactionDatabaseHelper.COLUMN_TYPE))
                transactions.add(Transaction(id, amount, description, date, type))
            }
            close()
        }
        db.close()
        return transactions
    }
}