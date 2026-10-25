package ir.ornix.passgen.feature.unlock.impl.presentation

import androidx.compose.runtime.Composable
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.error_incorect_biometric
import ir.ornix.passgen.core.ui.error_incorect_secret
import ir.ornix.passgen.core.ui.error_unknown
import ir.ornix.passgen.core.ui.secret_password
import ir.ornix.passgen.core.ui.secret_pattern
import ir.ornix.passgen.core.ui.secret_pin
import org.jetbrains.compose.resources.stringResource

internal sealed interface AuthenticationError {
    data class InvalidCredential(val localAuthType: LocalAuthType) : AuthenticationError
    data object InvalidBiometric : AuthenticationError
    data object Unknown : AuthenticationError
}

@Composable
internal fun AuthenticationError.asString(): String = when (this) {
    is AuthenticationError.Unknown ->
        stringResource(Res.string.error_unknown)

    is AuthenticationError.InvalidCredential ->
        stringResource(Res.string.error_incorect_secret, localAuthType.asString())

    is AuthenticationError.InvalidBiometric ->
        stringResource(Res.string.error_incorect_biometric)
}

@Composable
private fun LocalAuthType.asString() = when (this) {
    LocalAuthType.NONE -> ""
    LocalAuthType.PIN -> stringResource(Res.string.secret_pin)
    LocalAuthType.PASSWORD -> stringResource(Res.string.secret_password)
    LocalAuthType.PATTERN -> stringResource(Res.string.secret_pattern)
}