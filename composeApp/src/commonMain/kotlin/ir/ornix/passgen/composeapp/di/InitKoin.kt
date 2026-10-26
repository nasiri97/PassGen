package ir.ornix.passgen.composeapp.di

import ir.ornix.passgen.core.data.di.settingsRepositoryModule
import ir.ornix.passgen.core.data.di.sqlDelightRepositoryModule
import ir.ornix.passgen.core.database.createDatabaseDriver
import org.koin.core.context.startKoin

suspend fun initKoin() {
    val driver = createDatabaseDriver()

    val koinApplication = startKoin {
        modules(
            appModule,
            settingsRepositoryModule,
            sqlDelightRepositoryModule(driver = driver)
        )
    }
}