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
import ir.ornix.passgen.core.model.PasswordStrengthLevel
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig
import ir.ornix.passgen.core.ui.Res
import ir.ornix.passgen.core.ui.strength_fair
import ir.ornix.passgen.core.ui.strength_fragile
import ir.ornix.passgen.core.ui.strength_good
import ir.ornix.passgen.core.ui.strength_robust
import ir.ornix.passgen.core.ui.strength_strong
import ir.ornix.passgen.core.ui.strength_unknown
import ir.ornix.passgen.core.ui.strength_weak
import org.jetbrains.compose.resources.stringResource

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
fun PasswordStrengthLevel.label(): String = when (this) {
    PasswordStrengthLevel.Unknown -> stringResource(Res.string.strength_unknown)
    PasswordStrengthLevel.Fragile -> stringResource(Res.string.strength_fragile)
    PasswordStrengthLevel.Weak -> stringResource(Res.string.strength_weak)
    PasswordStrengthLevel.Fair -> stringResource(Res.string.strength_fair)
    PasswordStrengthLevel.Good -> stringResource(Res.string.strength_good)
    PasswordStrengthLevel.Strong -> stringResource(Res.string.strength_strong)
    PasswordStrengthLevel.Robust -> stringResource(Res.string.strength_robust)
}

@Composable
fun PassGenConfig.strengthColor(): Color = strengthLevel.strengthColor(isSystemInDarkTheme())
