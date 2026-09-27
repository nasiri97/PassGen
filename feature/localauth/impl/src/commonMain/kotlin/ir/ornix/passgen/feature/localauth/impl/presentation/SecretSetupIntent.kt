package ir.ornix.passgen.feature.localauth.impl.presentation

import ir.ornix.passgen.core.domain.LocalAuthType

sealed interface SecretSetupIntent {

    data class LocalAuthTypeSelected(val localAuthType: LocalAuthType) : SecretSetupIntent

    data class SecretInserted(val secret: String) : SecretSetupIntent

    data object SetupCancelled : SecretSetupIntent
}