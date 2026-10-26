package ir.ornix.passgen.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual suspend fun createDatabaseDriver(): SqlDriver {
    val worker = Worker(
        js(
            """
            new URL(
                "@cashapp/sqldelight-sqljs-worker/sqljs.worker.js",
                import.meta.url
            )
            """
        )
    )

    return WebWorkerDriver(worker).also {
        PassGenDatabase.Schema.create(it).await()
    }
}