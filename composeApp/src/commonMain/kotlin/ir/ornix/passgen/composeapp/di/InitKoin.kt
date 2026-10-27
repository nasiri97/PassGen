package ir.ornix.passgen.composeapp.di

import ir.ornix.passgen.core.data.di.settingsRepositoryModule
import ir.ornix.passgen.core.data.di.sqlDelightRepositoryModule
import ir.ornix.passgen.core.database.createDatabaseDriver
import ir.ornix.passgen.core.domain.di.domainModule
import org.koin.core.context.startKoin

suspend fun initKoin() {
    val driver = createDatabaseDriver()

    val koinApplication = startKoin {
        modules(
            appModule,
            domainModule,
            settingsRepositoryModule,
            sqlDelightRepositoryModule(driver = driver)
        )
    }
}