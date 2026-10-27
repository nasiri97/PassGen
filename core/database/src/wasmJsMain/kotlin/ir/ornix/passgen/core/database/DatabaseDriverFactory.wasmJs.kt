package ir.ornix.passgen.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => new Worker(new URL('@cashapp/sqldelight-sqljs-worker/sqljs.worker.js', import.meta.url))")
private external fun createWorker(): Worker

@OptIn(ExperimentalWasmJsInterop::class)
actual suspend fun createDatabaseDriver(): SqlDriver {
    val worker: Worker = createWorker()
    val driver = WebWorkerDriver(worker.unsafeCast())
    PassGenDatabase.Schema.create(driver).await()
    return driver
}