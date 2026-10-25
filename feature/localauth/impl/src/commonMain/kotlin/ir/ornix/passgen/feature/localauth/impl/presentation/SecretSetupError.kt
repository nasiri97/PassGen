package ir.ornix.passgen.feature.localauth.impl.presentation

import androidx.compose.runtime.Composable
import ir.ornix.passgen.core.domain.LocalAuthType
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.error_passwords_not_match
import ir.ornix.passgen.core.ui.error_patterns_not_match
import ir.ornix.passgen.core.ui.error_pins_not_match
import ir.ornix.passgen.core.ui.error_unknown
import org.jetbrains.compose.resources.stringResource

internal sealed interface SecretSetupError {
    data class SecretsNotMatch(val localAuthType: LocalAuthType) : SecretSetupError
    data object Unknown : SecretSetupError
}

@Composable
internal fun SecretSetupError.asString() = when (this) {
    is SecretSetupError.Unknown ->
        stringResource(Res.string.error_unknown)

    is SecretSetupError.SecretsNotMatch ->
        when (this.localAuthType) {
            LocalAuthType.NONE -> ""
            LocalAuthType.PIN -> stringResource(Res.string.error_pins_not_match)
            LocalAuthType.PASSWORD -> stringResource(Res.string.error_passwords_not_match)
            LocalAuthType.PATTERN -> stringResource(Res.string.error_patterns_not_match)
        }
}