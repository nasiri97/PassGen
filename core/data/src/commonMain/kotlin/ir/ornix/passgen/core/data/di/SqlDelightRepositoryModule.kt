package ir.ornix.passgen.core.data.di

import app.cash.sqldelight.db.SqlDriver
import ir.ornix.passgen.core.data.repository.SqlDelightCredentialRepository
import ir.ornix.passgen.core.data.repository.SqlDelightLocalAuthRepository
import ir.ornix.passgen.core.data.repository.SqlDelightPassGenConfigRepository
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.CredentialRepository
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import org.koin.dsl.bind
import org.koin.dsl.module

fun sqlDelightRepositoryModule(driver: SqlDriver) = module {
    single<SqlDriver> { driver }
    single { PassGenDatabase(get()) }

    single { SqlDelightPassGenConfigRepository(get()) } bind PassGenConfigRepository::class
    single { SqlDelightCredentialRepository(get()) } bind CredentialRepository::class
    single { SqlDelightLocalAuthRepository(get()) } bind LocalAuthRepository::class
}