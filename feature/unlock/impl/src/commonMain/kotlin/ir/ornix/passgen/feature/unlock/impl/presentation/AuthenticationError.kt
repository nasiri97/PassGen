package ir.ornix.passgen.feature.unlock.impl.presentation

import androidx.compose.runtime.Composable
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticationFailureReason
import ir.ornix.passgen.core.model.localauth.LocalAuthType
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.error_incorect_secret
import ir.ornix.passgen.core.ui.error_unknown
import ir.ornix.passgen.core.ui.util.toStringResource
import org.jetbrains.compose.resources.stringResource

internal sealed interface AuthenticationError {
    data class InvalidCredential(val localAuthType: LocalAuthType) : AuthenticationError

    data class BiometricAuthenticationError(
        val reason: PlatformAuthenticationFailureReason
    ) : AuthenticationError

    data object Unknown : AuthenticationError
}

@Composable
internal fun AuthenticationError.asString(): String = when (this) {
    is AuthenticationError.Unknown ->
        stringResource(Res.string.error_unknown)

    is AuthenticationError.InvalidCredential ->
        stringResource(
            Res.string.error_incorect_secret,
            localAuthType.toStringResource()?.let { stringResource(it) } ?: "")

    is AuthenticationError.BiometricAuthenticationError ->
        stringResource(this.reason.toStringResource())
}