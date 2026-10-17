package ir.ornix.passgen.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual fun createDatabaseDriver(): SqlDriver {
    val dbFile = File("passgen.db")
    val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:passgen.db")
    if (!dbFile.exists()) {
        PassGenDatabase.Schema.create(driver)
    }
    return driver
}
