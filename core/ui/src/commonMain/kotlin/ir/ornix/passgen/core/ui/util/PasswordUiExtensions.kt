package ir.ornix.passgen.core.ui.util

import androidx.compose.ui.graphics.Color
import ir.ornix.passgen.core.model.PasswordStrengthLevel
import ir.ornix.passgen.core.model.passgenconfig.PassGenConfig

fun PassGenConfig.strengthColor(): Color = when (strengthLevel) {
    PasswordStrengthLevel.Fragile -> Color.Red
    PasswordStrengthLevel.Weak -> Color(0xFFFFA500)
    PasswordStrengthLevel.Fair -> Color.Yellow
    PasswordStrengthLevel.Good -> Color.Green
    PasswordStrengthLevel.Strong -> Color(0xFF006400)
    PasswordStrengthLevel.Robust -> Color.Blue
}
