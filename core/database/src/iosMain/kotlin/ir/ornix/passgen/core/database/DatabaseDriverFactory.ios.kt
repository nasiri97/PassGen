package ir.ornix.passgen.core.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual suspend fun createDatabaseDriver(): SqlDriver {
    return NativeSqliteDriver(
        schema = PassGenDatabase.Schema.synchronous(),
        name = DATABASE_NAME
    )
}
