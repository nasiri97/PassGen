package ir.ornix.passgen.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createDatabaseDriver(): SqlDriver {
    return NativeSqliteDriver(
        schema = PassGenDatabase.Schema,
        name = "passgen.db"
    )
}
