package com.example.moneymanager.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create the transactions table
        val createTransactionsTable = """
            CREATE TABLE $TABLE_TRANSACTIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_DATE LONG,
                $COLUMN_TYPE TEXT,
                $COLUMN_GUID TEXT UNIQUE
            );
        """.trimIndent()
        db.execSQL(createTransactionsTable)

        // Create the accounts table
        val createAccountsTable = """
            CREATE TABLE $TABLE_ACCOUNTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TYPE TEXT,
                $COLUMN_BALANCE REAL,
                $COLUMN_GUID TEXT UNIQUE
            );
        """.trimIndent()
        db.execSQL(createAccountsTable)

        // Create the budgets table with foreign key
        val createBudgetsTable = """
            CREATE TABLE $TABLE_BUDGETS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL,
                $COLUMN_NAME TEXT,
                $COLUMN_PERCENT REAL,
                $COLUMN_GUID TEXT UNIQUE,
                $COLUMN_CATEGORY_ID INTEGER,
                FOREIGN KEY($COLUMN_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($COLUMN_ID)
            );
        """.trimIndent()
        db.execSQL(createBudgetsTable)

        // Create the category table
        val createCategoryTableStatement = """
            CREATE TABLE $TABLE_CATEGORY (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_GUID TEXT UNIQUE
            );
        """.trimIndent()
        db.execSQL(createCategoryTableStatement)
        insertCategories(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop and recreate tables if upgrading
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BUDGETS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORY")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON") // Enable foreign key constraints
    }

    private fun insertCategories(db: SQLiteDatabase?) {
        val categories = listOf(
            Category(0, "Auto & Transport", "CAT-7829f71c-2e8c-afa5-2f55-fa3634b89874"),
            Category(0, "Auto Insurance", "CAT-de7c2dc7-90e6-85a2-6509-5ec10942e887"),
            Category(0, "Auto Payment", "CAT-cb93691a-684d-b326-4c32-f8abaecfde90"),
            Category(0, "Gas", "CAT-b6d63a19-30a7-e852-2703-bdfb4072289e"),
            Category(0, "Parking", "CAT-726da718-d572-1e4d-7c3f-0b8b5370fe71")
        )

        categories.forEach { category ->
            val values = ContentValues().apply {
                put(COLUMN_NAME, category.name)
                put(COLUMN_GUID, category.guid)
            }
            db?.insert(TABLE_CATEGORY, null, values)
        }
    }

    companion object {
        private const val DATABASE_NAME = "transactions.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_TRANSACTIONS = "transactions"
        const val TABLE_ACCOUNTS = "accounts"
        const val TABLE_BUDGETS = "budgets"
        const val TABLE_CATEGORY = "category"

        const val COLUMN_ID = "id"
        const val COLUMN_AMOUNT = "amount"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DATE = "date"
        const val COLUMN_TYPE = "type"
        const val COLUMN_GUID = "guid"
        const val COLUMN_NAME = "name"
        const val COLUMN_PERCENT = "percent_spent"
        const val COLUMN_BALANCE = "balance"
        const val COLUMN_BUDGET_ID = "budget_id"
        const val COLUMN_CATEGORY_ID = "category_id"
    }
}
