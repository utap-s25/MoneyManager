package com.example.moneymanager.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

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
                $COLUMN_NAME TEXT,
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
                $COLUMN_CATEGORY_ID TEXT,
                FOREIGN KEY($COLUMN_CATEGORY_ID) REFERENCES $TABLE_CATEGORY($COLUMN_GUID)
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
        Log.d("insertCategories", "insertingCategories")
        val categories = listOf(
            Category(0, "Auto & Transport", "CAT-7829f71c-2e8c-afa5-2f55-fa3634b89874"),
            Category(0, "Unknown", "CAT-d7851c65-3353-e490-1953-fb9235e681e4"),
            Category(0, "Auto Insurance", "CAT-de7c2dc7-90e6-85a2-6509-5ec10942e887"),
            Category(0, "Auto Payment", "CAT-cb93691a-684d-b326-4c32-f8abaecfde90"),
            Category(0, "Gas", "CAT-b6d63a19-30a7-e852-2703-bdfb4072289e"),
            Category(0, "Parking", "CAT-726da718-d572-1e4d-7c3f-0b8b5370fe71"),
            Category(0, "Public Transportation", "CAT-19e6ec13-83ed-5511-7f75-3688d3f97a8e"),
            Category(0, "Service & Parts", "CAT-67a20fb1-1a61-dc27-3fff-f28fa904025f"),
            Category(0, "Bills & Utilities", "CAT-79b02f2f-2adc-88f0-ac2b-4e71ead9cfc8"),
            Category(0, "Domain Names", "CAT-e7c5ebc7-f73d-955a-db06-3724bbcf6faa"),
            Category(0, "Fraud Protection", "CAT-5f7f2084-6f8a-d927-6fa7-8e699912432a"),
            Category(0, "Home Phone", "CAT-60d81dd3-8ac1-a6fb-b181-b736dbb42be3"),
            Category(0, "Hosting", "CAT-b74fdd98-4391-8015-eafa-e9ca0fad3bee"),
            Category(0, "Internet", "CAT-78d29c63-54db-197d-9851-feeb94cf6e10"),
            Category(0, "Mobile Phone", "CAT-b4789667-6acc-a112-975e-15746003ed61"),
            Category(0, "Television", "CAT-b316e683-c898-6497-a476-6bc48d12e51d"),
            Category(0, "Utilities", "CAT-56a2979d-d6df-25da-f357-06282f08208e"),
            Category(0, "Business Services", "CAT-94b11142-e97b-941a-f67f-6e18d246a23f"),
            Category(0, "Advertising", "CAT-e6682ebe-f239-c654-8233-0970b94cc162"),
            Category(0, "Legal", "CAT-13c1e57c-3749-41b4-3bc4-b1b334adab85"),
            Category(0, "Office Supplies", "CAT-97a2b899-5a32-4196-b87c-a8e65d8a9849"),
            Category(0, "Printing", "CAT-3b09fb0b-3296-c76c-2bbd-a2bce0012d83"),
            Category(0, "Shipping", "CAT-748efd05-e27a-5c79-1217-681a0ec5cc67"),
            Category(0, "Education", "CAT-bf5c9cca-c96b-b50d-440d-38d9adfda5b0"),
            Category(0, "Books & Supplies", "CAT-167d816f-c68e-9988-e064-173dc6417a90"),
            Category(0, "Student Loan", "CAT-2ce4972e-5620-57be-8475-d084edf88888")
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
        private const val DATABASE_NAME = "moneymanager.db"
        private const val DATABASE_VERSION = 17

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
        const val COLUMN_CATEGORY_ID = "category_id"
    }
}
