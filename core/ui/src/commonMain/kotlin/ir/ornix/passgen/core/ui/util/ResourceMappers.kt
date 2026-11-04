package ir.ornix.passgen.core.ui.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import ir.ornix.passgen.core.designsystem.theme.PassStrengthFairDark
import ir.ornix.passgen.core.designsystem.theme.PassStrengthFairLight
import ir.ornix.passgen.core.designsystem.theme.PassStrengthFragileDark
import ir.ornix.passgen.core.designsystem.theme.PassStrengthFragileLight
import ir.ornix.passgen.core.designsystem.theme.PassStrengthGoodDark
import ir.ornix.passgen.core.designsystem.theme.PassStrengthGoodLight
import ir.ornix.passgen.core.designsystem.theme.PassStrengthRobustDark
import ir.ornix.passgen.core.designsystem.theme.PassStrengthRobustLight
import ir.ornix.passgen.core.designsystem.theme.PassStrengthStrongDark
import ir.ornix.passgen.core.designsystem.theme.PassStrengthStrongLight
import ir.ornix.passgen.core.designsystem.theme.PassStrengthUnknownDark
import ir.ornix.passgen.core.designsystem.theme.PassStrengthUnknownLight
import ir.ornix.passgen.core.designsystem.theme.PassStrengthWeakDark
import ir.ornix.passgen.core.designsystem.theme.PassStrengthWeakLight
import ir.ornix.passgen.core.domain.platformauth.PlatformAuthenticationFailureReason
import ir.ornix.passgen.core.model.PasswordStrengthLevel
import ir.ornix.passgen.core.model.localauth.LocalAuthType
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.auth_error_hardware_unavailable
import ir.ornix.passgen.core.ui.auth_error_not_available
import ir.ornix.passgen.core.ui.auth_error_not_enrolled
import ir.ornix.passgen.core.ui.auth_error_permanently_locked
import ir.ornix.passgen.core.ui.auth_error_security_update_required
import ir.ornix.passgen.core.ui.auth_error_temporarily_locked
import ir.ornix.passgen.core.ui.auth_error_unknown
import ir.ornix.passgen.core.ui.secret_password
import ir.ornix.passgen.core.ui.secret_pattern
import ir.ornix.passgen.core.ui.secret_pin
import ir.ornix.passgen.core.ui.strength_fair
import ir.ornix.passgen.core.ui.strength_fragile
import ir.ornix.passgen.core.ui.strength_good
import ir.ornix.passgen.core.ui.strength_robust
import ir.ornix.passgen.core.ui.strength_strong
import ir.ornix.passgen.core.ui.strength_unknown
import ir.ornix.passgen.core.ui.strength_weak
import org.jetbrains.compose.resources.StringResource

fun LocalAuthType.toStringResource() = when (this) {
    LocalAuthType.NONE -> null
    LocalAuthType.PIN -> Res.string.secret_pin
    LocalAuthType.PASSWORD -> Res.string.secret_password
    LocalAuthType.PATTERN -> Res.string.secret_pattern
}

fun PlatformAuthenticationFailureReason.toStringResource(): StringResource = when (this) {
    PlatformAuthenticationFailureReason.NotAvailable -> Res.string.auth_error_not_available
    PlatformAuthenticationFailureReason.HardwareUnavailable -> Res.string.auth_error_hardware_unavailable
    PlatformAuthenticationFailureReason.NotEnrolled -> Res.string.auth_error_not_enrolled
    PlatformAuthenticationFailureReason.TemporarilyLocked -> Res.string.auth_error_temporarily_locked
    PlatformAuthenticationFailureReason.PermanentlyLocked -> Res.string.auth_error_permanently_locked
    PlatformAuthenticationFailureReason.SecurityUpdateRequired -> Res.string.auth_error_security_update_required
    PlatformAuthenticationFailureReason.Unknown -> Res.string.auth_error_unknown
}

fun PasswordStrengthLevel.toStringResource(): StringResource = when (this) {
    PasswordStrengthLevel.Unknown -> Res.string.strength_unknown
    PasswordStrengthLevel.Fragile -> Res.string.strength_fragile
    PasswordStrengthLevel.Weak -> Res.string.strength_weak
    PasswordStrengthLevel.Fair -> Res.string.strength_fair
    PasswordStrengthLevel.Good -> Res.string.strength_good
    PasswordStrengthLevel.Strong -> Res.string.strength_strong
    PasswordStrengthLevel.Robust -> Res.string.strength_robust
}

fun PasswordStrengthLevel.strengthColor(isDark: Boolean = false): Color = when (this) {
    PasswordStrengthLevel.Unknown -> if (isDark) PassStrengthUnknownDark else PassStrengthUnknownLight
    PasswordStrengthLevel.Fragile -> if (isDark) PassStrengthFragileDark else PassStrengthFragileLight
    PasswordStrengthLevel.Weak -> if (isDark) PassStrengthWeakDark else PassStrengthWeakLight
    PasswordStrengthLevel.Fair -> if (isDark) PassStrengthFairDark else PassStrengthFairLight
    PasswordStrengthLevel.Good -> if (isDark) PassStrengthGoodDark else PassStrengthGoodLight
    PasswordStrengthLevel.Strong -> if (isDark) PassStrengthStrongDark else PassStrengthStrongLight
    PasswordStrengthLevel.Robust -> if (isDark) PassStrengthRobustDark else PassStrengthRobustLight
}

@Composable
fun PassGenConfig.strengthColor(): Color = strengthLevel.strengthColor(isSystemInDarkTheme())
