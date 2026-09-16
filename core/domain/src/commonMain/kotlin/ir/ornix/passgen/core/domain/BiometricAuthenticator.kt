package ir.ornix.passgen.core.domain

import kotlinx.coroutines.flow.StateFlow

interface BiometricAuthenticator {

    val defaultDescription: String

    fun isBiometricAvailable(): StateFlow<Boolean>

    fun authenticate(
        title: String = "Verify it's you",
        subtitle: String = "",
        description: String = defaultDescription,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}
