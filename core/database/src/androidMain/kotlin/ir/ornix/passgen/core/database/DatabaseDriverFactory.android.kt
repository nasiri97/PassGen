package ir.ornix.passgen.core.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import ir.ornix.passgen.core.common.AppContextProvider

actual fun createDatabaseDriver(): SqlDriver {
    val context = AppContextProvider.applicationContext
        ?: error("Database context is not initialized. Call initDatabaseContext(context) before creating database.")
    return AndroidSqliteDriver(
        schema = PassGenDatabase.Schema.synchronous(),
        context = context,
        name = DATABASE_NAME
    )
}
