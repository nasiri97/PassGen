package ir.ornix.passgen.feature.localauth.impl.unlocking.presentation

sealed interface UnlockingGateIntent {
    data object BiometricClicked : UnlockingGateIntent
    data class SecretSubmitted(val secret: String) : UnlockingGateIntent
}