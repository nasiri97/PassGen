package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.BiometricAuthenticator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DefaultBiometricAuthenticator : BiometricAuthenticator {

    override val defaultDescription: String = "Use your fingerprint to continue"

    override fun isBiometricAvailable(): StateFlow<Boolean> = MutableStateFlow(false)

    override fun authenticate(
        title: String,
        subtitle: String,
        description: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        onError("Biometric authentication is not supported on this platform.")
    }
}
