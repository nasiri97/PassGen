package ir.ornix.passgen.composeapp.di

import com.russhwolf.settings.Settings
import ir.ornix.passgen.core.data.SettingsAccountRepository
import ir.ornix.passgen.core.data.SettingsMasterKeyRepository
import ir.ornix.passgen.core.data.SettingsPassGenConfigRepository
import ir.ornix.passgen.core.domain.AccountRepository
import ir.ornix.passgen.core.domain.MasterKeyRepository
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.GenerateRandomPasswordUseCase
import ir.ornix.passgen.core.domain.account.SaveAccountUseCase
import ir.ornix.passgen.core.domain.masterkey.ClearMasterKeyUseCase
import ir.ornix.passgen.core.domain.masterkey.RetrieveMasterKeyUseCase
import ir.ornix.passgen.core.domain.masterkey.SaveMasterKeyUseCase
import ir.ornix.passgen.core.domain.passgenconfig.AddPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.GetAllPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.RemovePassGenConfigUseCase
import ir.ornix.passgen.feature.home.impl.presentation.HomeViewModel
import ir.ornix.passgen.feature.setup.impl.presentation.SetupViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { Settings() }
    
    singleOf(::SettingsMasterKeyRepository) bind MasterKeyRepository::class
    singleOf(::SettingsPassGenConfigRepository) bind PassGenConfigRepository::class
    singleOf(::SettingsAccountRepository) bind AccountRepository::class
    
    factoryOf(::AddPassGenConfigUseCase)
    factoryOf(::GetAllPassGenConfigsUseCase)
    factoryOf(::RemovePassGenConfigUseCase)
    factoryOf(::GenerateRandomPasswordUseCase)
    factoryOf(::SaveAccountUseCase)
    
    factoryOf(::SaveMasterKeyUseCase)
    factoryOf(::RetrieveMasterKeyUseCase)
    factoryOf(::ClearMasterKeyUseCase)
    
    viewModelOf(::HomeViewModel)
    viewModelOf(::SetupViewModel)
}
