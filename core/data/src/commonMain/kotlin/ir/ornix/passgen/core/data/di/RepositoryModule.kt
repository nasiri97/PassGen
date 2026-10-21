package ir.ornix.passgen.core.data.di

import ir.ornix.passgen.core.data.repository.SettingsAppConfigRepository
import ir.ornix.passgen.core.data.repository.SqlDelightAccountRepository
import ir.ornix.passgen.core.data.repository.SqlDelightLocalAuthRepository
import ir.ornix.passgen.core.data.repository.SqlDelightPassGenConfigRepository
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.database.createDatabaseDriver
import ir.ornix.passgen.core.domain.AccountRepository
import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule: Module = module {
    // Settings
    single { SettingsAppConfigRepository(get()) } bind AppConfigRepository::class

    // SqlDelight
    single { createDatabaseDriver() }
    single { PassGenDatabase(get()) }

    single { SqlDelightPassGenConfigRepository(get(), get()) } bind PassGenConfigRepository::class
    single { SqlDelightAccountRepository(get(), get()) } bind AccountRepository::class
    single { SqlDelightLocalAuthRepository(get(), get()) } bind LocalAuthRepository::class
}
