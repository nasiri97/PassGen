package ir.ornix.passgen.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

private var androidDatabaseContext: Context? = null

fun initDatabaseContext(context: Context) {
    androidDatabaseContext = context.applicationContext
}

actual fun createDatabaseDriver(): SqlDriver {
    val context = androidDatabaseContext
        ?: error("Database context is not initialized. Call initDatabaseContext(context) before creating database.")
    return AndroidSqliteDriver(
        schema = PassGenDatabase.Schema,
        context = context,
        name = "passgen.db"
    )
}
