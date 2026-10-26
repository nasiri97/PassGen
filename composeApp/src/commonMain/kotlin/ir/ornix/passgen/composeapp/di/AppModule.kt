package ir.ornix.passgen.composeapp.di

import ir.ornix.passgen.composeapp.navigation.NavViewModelStoreProvider
import ir.ornix.passgen.core.data.PlatformCrypto
import ir.ornix.passgen.core.data.PlatformHmacSigner
import ir.ornix.passgen.core.data.getPlatformBiometricAuthenticator
import ir.ornix.passgen.core.domain.BiometricAuthenticator
import ir.ornix.passgen.core.domain.Crypto
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.appconfig.GetLanguageUseCase
import ir.ornix.passgen.core.domain.appconfig.GetThemeUseCase
import ir.ornix.passgen.core.domain.appconfig.GetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.appconfig.IsCopyEnabledUseCase
import ir.ornix.passgen.core.domain.appconfig.SetCopyEnabledUseCase
import ir.ornix.passgen.core.domain.appconfig.SetLanguageUseCase
import ir.ornix.passgen.core.domain.appconfig.SetThemeUseCase
import ir.ornix.passgen.core.domain.appconfig.SetVisibilityTimeoutUseCase
import ir.ornix.passgen.core.domain.credential.DeleteCredentialUseCase
import ir.ornix.passgen.core.domain.credential.GetAllCredentialsUseCase
import ir.ornix.passgen.core.domain.credential.SaveCredentialUseCase
import ir.ornix.passgen.core.domain.localauth.GetLocalAuthTypeUseCase
import ir.ornix.passgen.core.domain.localauth.IsBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.IsUnlockingRequiredUseCase
import ir.ornix.passgen.core.domain.localauth.SaveLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.localauth.SetBiometricEnabledUseCase
import ir.ornix.passgen.core.domain.localauth.ValidateLocalAuthSecretUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateKDFPassUseCase
import ir.ornix.passgen.core.domain.passgen.GenerateRandomPassUseCase
import ir.ornix.passgen.core.domain.passgenconfig.kdf.AddKdfPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.kdf.GetAllKdfPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.kdf.RemoveKdfPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.random.AddRandomPassGenConfigUseCase
import ir.ornix.passgen.core.domain.passgenconfig.random.GetAllRandomPassGenConfigsUseCase
import ir.ornix.passgen.core.domain.passgenconfig.random.RemoveRandomPassGenConfigUseCase
import ir.ornix.passgen.feature.config.impl.di.configModule
import ir.ornix.passgen.feature.credentials.impl.presentation.CredentialsViewModel
import ir.ornix.passgen.feature.home.impl.presentation.HomeViewModel
import ir.ornix.passgen.feature.localauth.impl.presentation.SecretSetupViewModel
import ir.ornix.passgen.feature.random.impl.presentation.RandomViewModel
import ir.ornix.passgen.feature.settings.impl.presentation.SettingsViewModel
import ir.ornix.passgen.feature.unlock.impl.presentation.UnlockingGateViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    includes(configModule)

    single { getPlatformBiometricAuthenticator() } bind BiometricAuthenticator::class
    single<HmacSigner> { PlatformHmacSigner }
    single<Crypto> { PlatformCrypto }

    factoryOf(::GenerateKDFPassUseCase)
    factoryOf(::GenerateRandomPassUseCase)

    factoryOf(::AddKdfPassGenConfigUseCase)
    factoryOf(::GetAllKdfPassGenConfigsUseCase)
    factoryOf(::RemoveKdfPassGenConfigUseCase)

    factoryOf(::AddRandomPassGenConfigUseCase)
    factoryOf(::GetAllRandomPassGenConfigsUseCase)
    factoryOf(::RemoveRandomPassGenConfigUseCase)

    factoryOf(::SaveCredentialUseCase)
    factoryOf(::GetAllCredentialsUseCase)
    factoryOf(::DeleteCredentialUseCase)

    factoryOf(::GetLocalAuthTypeUseCase)
    factoryOf(::SaveLocalAuthSecretUseCase)
    factoryOf(::ValidateLocalAuthSecretUseCase)
    factoryOf(::IsBiometricEnabledUseCase)
    factoryOf(::SetBiometricEnabledUseCase)
    factoryOf(::IsUnlockingRequiredUseCase)
    factoryOf(::GetLanguageUseCase)
    factoryOf(::SetLanguageUseCase)
    factoryOf(::GetThemeUseCase)
    factoryOf(::SetThemeUseCase)
    factoryOf(::IsCopyEnabledUseCase)
    factoryOf(::SetCopyEnabledUseCase)
    factoryOf(::GetVisibilityTimeoutUseCase)
    factoryOf(::SetVisibilityTimeoutUseCase)

    viewModelOf(::HomeViewModel)
    viewModelOf(::RandomViewModel)
    viewModelOf(::CredentialsViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::UnlockingGateViewModel)
    viewModelOf(::SecretSetupViewModel)
    viewModelOf(::NavViewModelStoreProvider)
}
