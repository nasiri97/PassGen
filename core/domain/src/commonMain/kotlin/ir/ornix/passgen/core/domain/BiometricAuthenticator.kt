package ir.ornix.passgen.core.domain

interface BiometricAuthenticator {

    val defaultDescription: String

    fun isBiometricAvailable(): Boolean
    fun authenticate(
        title: String = "Verify it's you",
        subtitle: String = "",
        description: String = defaultDescription,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}
