package ir.ornix.passgen.core.database

import app.cash.sqldelight.db.SqlDriver

internal const val DATABASE_NAME = "passgen.db"

expect fun createDatabaseDriver(): SqlDriver

fun createDatabase(): PassGenDatabase {
    val driver = createDatabaseDriver()
    return PassGenDatabase(driver)
}
