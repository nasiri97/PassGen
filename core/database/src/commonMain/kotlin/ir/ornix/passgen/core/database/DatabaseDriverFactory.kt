package ir.ornix.passgen.core.database

import app.cash.sqldelight.db.SqlDriver

internal const val DATABASE_NAME = "passgen.db"

expect suspend fun createDatabaseDriver(): SqlDriver