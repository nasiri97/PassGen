package ir.ornix.passgen.feature.unlock.impl.presentation

sealed interface UnlockingGateIntent {
    data object BiometricClicked : UnlockingGateIntent
    data class SecretSubmitted(val secret: String) : UnlockingGateIntent
}