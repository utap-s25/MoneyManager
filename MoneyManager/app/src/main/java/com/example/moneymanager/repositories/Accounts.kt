package com.example.moneymanager.repositories

import android.content.ContentValues
import android.content.Context
import com.example.moneymanager.database.LocalAccount
import com.example.moneymanager.database.DatabaseHelper

class Accounts(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insertAccount(guid: String, type: String, balance: Double) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_GUID, guid)
            put(DatabaseHelper.COLUMN_TYPE, type)
            put(DatabaseHelper.COLUMN_BALANCE, balance)
        }
        db.insertWithOnConflict(
            DatabaseHelper.TABLE_ACCOUNTS,
            null,
            values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
        db.close()
    }

    fun getAllAccounts(): List<LocalAccount> {
        val accounts = mutableListOf<LocalAccount>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_ACCOUNTS,
            null, null, null, null, null, null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val guid = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_GUID))
                val type = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE))
                val balance = getDouble(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BALANCE))
                accounts.add(LocalAccount(id, guid, type, balance))
            }
            close()
        }
        db.close()
        return accounts
    }
}