package ir.ornix.passgen.core.database

import app.cash.sqldelight.db.SqlDriver

expect fun createDatabaseDriver(): SqlDriver

fun createDatabase(): PassGenDatabase {
    val driver = createDatabaseDriver()
    return PassGenDatabase(driver)
}
