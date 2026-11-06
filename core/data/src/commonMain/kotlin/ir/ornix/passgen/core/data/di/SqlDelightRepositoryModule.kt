package ir.ornix.passgen.core.data.di

import app.cash.sqldelight.db.SqlDriver
import ir.ornix.passgen.core.data.repository.SqlDelightCredentialRepository
import ir.ornix.passgen.core.data.repository.SqlDelightKdfPassGenConfigRepository
import ir.ornix.passgen.core.data.repository.SqlDelightLocalAuthRepository
import ir.ornix.passgen.core.data.repository.SqlDelightRandomPassGenConfigRepository
import ir.ornix.passgen.core.database.PassGenDatabase
import ir.ornix.passgen.core.domain.CredentialRepository
import ir.ornix.passgen.core.domain.KdfPassGenConfigRepository
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.RandomPassGenConfigRepository
import org.koin.dsl.bind
import org.koin.dsl.module

fun sqlDelightRepositoryModule(driver: SqlDriver) = module {
    single<SqlDriver> { driver }
    single { PassGenDatabase(get()) }

    single { SqlDelightKdfPassGenConfigRepository(get()) } bind KdfPassGenConfigRepository::class
    single { SqlDelightRandomPassGenConfigRepository(get()) } bind RandomPassGenConfigRepository::class
    single { SqlDelightCredentialRepository(get()) } bind CredentialRepository::class
    single { SqlDelightLocalAuthRepository(get()) } bind LocalAuthRepository::class
}