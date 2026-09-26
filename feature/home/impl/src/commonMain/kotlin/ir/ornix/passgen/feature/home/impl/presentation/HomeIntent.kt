package ir.ornix.passgen.feature.home.impl.presentation

import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.domain.passgenconfig.model.PassGenConfig
import ir.ornix.passgen.core.model.Account

sealed interface HomeIntent {
    data class InputChanged(val input: String) : HomeIntent
    data object AddNewConfigClicked : HomeIntent

    data class CreateConfig(
        val config: KDFPassGenConfig,
        val rawKey: ByteArray
    ) : HomeIntent

    data class RemoveConfig(val passGenConfig: PassGenConfig) : HomeIntent

    data object CancelCreatingNewConfigClicked : HomeIntent

    data class SaveAccount(val account: Account) : HomeIntent
}