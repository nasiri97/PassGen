package ir.ornix.passgen.core.domain.platformauth

sealed interface PlatformAuthenticationResult {
    data object Success : PlatformAuthenticationResult
    data object Cancelled : PlatformAuthenticationResult
    data class Error(val reason: PlatformAuthenticationFailureReason) : PlatformAuthenticationResult
}