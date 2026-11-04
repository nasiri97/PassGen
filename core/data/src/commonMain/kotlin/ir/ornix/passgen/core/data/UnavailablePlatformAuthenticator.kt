package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.platformauth.AuthenticationAvailability
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticationFailureReason
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticationResult
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticator

class UnavailablePlatformAuthenticator : PlatformAuthenticator {

    override fun isAvailable(): AuthenticationAvailability =
        AuthenticationAvailability.Unavailable(PlatformAuthenticationFailureReason.NotAvailable)

    override fun authenticate(
        title: String,
        subtitle: String,
        description: String,
        onResult: (PlatformAuthenticationResult) -> Unit
    ) {
        onResult(
            PlatformAuthenticationResult.Error(
                PlatformAuthenticationFailureReason.NotAvailable
            )
        )
    }
}
