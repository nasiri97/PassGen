package ir.ornix.passgen.composeapp.di

import com.russhwolf.settings.Settings
import ir.ornix.passgen.core.data.SecureHmacSigner
import ir.ornix.passgen.core.data.SettingsAccountRepository
import ir.ornix.passgen.core.data.SettingsAppConfigRepository
import ir.ornix.passgen.core.data.SettingsLocalAuthRepository
import ir.ornix.passgen.core.data.SettingsPassGenConfigRepository
import ir.ornix.passgen.core.data.getPlatformBiometricAuthenticator
import ir.ornix.passgen.core.domain.AccountRepository
import ir.ornix.passgen.core.domain.AppConfigRepository
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.account.SaveAccountUseCase
import ir.ornix.passgen.core.domain.appconfig.IsFirstLaunchUseCase
import ir.ornix.passgen.core.domain.appconfig.SetFirstLaunchUseCase
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.IsUnlockingRequiredUseCase
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.localauth.SetBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.ValidateLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateKDFPassUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateRandomPassUseCase
import ir.ornix.passgen.core.domain.passgenconfig.AddPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.GetAllPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.RemovePassGenConfigUseCase
import ir.ornix.passgen.feature.home.impl.presentation.HomeViewModel
import ir.ornix.passgen.feature.localauth.impl.secretsetup.SecretSetupViewModel
import ir.ornix.passgen.feature.localauth.impl.unlocking.UnlockingGateViewModel
import ir.ornix.passgen.feature.settings.impl.presentation.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { Settings() }

    singleOf(::SecureHmacSigner) bind HmacSigner::class
    singleOf(::SettingsAppConfigRepository) bind AppConfigRepository::class
    singleOf(::SettingsPassGenConfigRepository) bind PassGenConfigRepository::class
    singleOf(::SettingsAccountRepository) bind AccountRepository::class
    single { SettingsLocalAuthRepository(get()) } bind LocalAuthRepository::class
    single { getPlatformBiometricAuthenticator() } bind BiometricAuthenticator::class

    factoryOf(::GenerateKDFPassUseCase)
    factoryOf(::AddPassGenConfigUseCase)
    factoryOf(::GetAllPassGenConfigsUseCase)
    factoryOf(::RemovePassGenConfigUseCase)
    factoryOf(::GenerateRandomPassUseCase)
    factoryOf(::SaveAccountUseCase)

    factoryOf(::GetLocalAuthTypeUseCase)
    factoryOf(::SaveLocalAuthSecretUseCase)
    factoryOf(::ValidateLocalAuthSecretUseCase)
    factoryOf(::IsBiometricEnabledUseCase)
    factoryOf(::SetBiometricEnabledUseCase)
    factoryOf(::IsFirstLaunchUseCase)
    factoryOf(::SetFirstLaunchUseCase)
    factoryOf(::IsUnlockingRequiredUseCase)

    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::UnlockingGateViewModel)
    viewModelOf(::SecretSetupViewModel)
}
