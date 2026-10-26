package ir.ornix.passgen.core.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual suspend fun createDatabaseDriver(): SqlDriver {
    return JdbcSqliteDriver(
        url = "jdbc:sqlite:$DATABASE_NAME",
        schema = PassGenDatabase.Schema.synchronous()
    )
}
