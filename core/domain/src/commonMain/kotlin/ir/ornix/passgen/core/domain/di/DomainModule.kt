package ir.ornix.passgen.core.domain.di

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
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    // appConfig
    factoryOf(::GetLanguageUseCase)
    factoryOf(::GetThemeUseCase)
    factoryOf(::GetVisibilityTimeoutUseCase)
    factoryOf(::IsCopyEnabledUseCase)
    factoryOf(::SetCopyEnabledUseCase)
    factoryOf(::SetLanguageUseCase)
    factoryOf(::SetThemeUseCase)
    factoryOf(::SetVisibilityTimeoutUseCase)

    // credential
    factoryOf(::DeleteCredentialUseCase)
    factoryOf(::GetAllCredentialsUseCase)
    factoryOf(::SaveCredentialUseCase)

    // localauth
    factoryOf(::GetLocalAuthTypeUseCase)
    factoryOf(::IsBiometricEnabledUseCase)
    factoryOf(::IsUnlockingRequiredUseCase)
    factoryOf(::SaveLocalAuthSecretUseCase)
    factoryOf(::SetBiometricEnabledUseCase)
    factoryOf(::ValidateLocalAuthSecretUseCase)

    // passgen
    factoryOf(::GenerateKDFPassUseCase)
    factoryOf(::GenerateRandomPassUseCase)

    // passgenconfig/kdf
    factoryOf(::AddKdfPassGenConfigUseCase)
    factoryOf(::GetAllKdfPassGenConfigsUseCase)
    factoryOf(::RemoveKdfPassGenConfigUseCase)

    // passgenconfig/random
    factoryOf(::AddRandomPassGenConfigUseCase)
    factoryOf(::GetAllRandomPassGenConfigsUseCase)
    factoryOf(::RemoveRandomPassGenConfigUseCase)
}