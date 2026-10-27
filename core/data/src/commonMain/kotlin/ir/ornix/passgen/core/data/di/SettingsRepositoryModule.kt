package ir.ornix.passgen.core.data.di

import com.russhwolf.settings.Settings
import ir.ornix.passgen.core.data.repository.SettingsAppConfigRepository
import ir.ornix.passgen.core.domain.AppConfigRepository
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsRepositoryModule: Module = module {
    single { Settings() }
    single { SettingsAppConfigRepository(get()) } bind AppConfigRepository::class
}
