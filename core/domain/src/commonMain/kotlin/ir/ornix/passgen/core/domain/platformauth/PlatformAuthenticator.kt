package ir.ornix.passgen.core.domain.platformauth

interface PlatformAuthenticator {

    fun isAvailable(): AuthenticationAvailability

    fun authenticate(
        title: String,
        subtitle: String,
        description: String,
        onResult: (PlatformAuthenticationResult) -> Unit
    )
}