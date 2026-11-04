package ir.ornix.passgen.core.domain.platformauth

sealed interface AuthenticationAvailability {
    val isAvailable: Boolean
        get() = (this is Available)

    data object Available : AuthenticationAvailability

    data class Unavailable(
        val reason: PlatformAuthenticationFailureReason
    ) : AuthenticationAvailability
}