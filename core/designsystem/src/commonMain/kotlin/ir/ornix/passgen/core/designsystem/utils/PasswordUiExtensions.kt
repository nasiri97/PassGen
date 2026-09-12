package ir.ornix.passgen.core.designsystem.utils

import androidx.compose.ui.graphics.Color
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.PasswordStrengthLevel

fun Password.strengthColor(): Color = when (strengthLevel) {
    PasswordStrengthLevel.Fragile -> Color.Red
    PasswordStrengthLevel.Weak -> Color(0xFFFFA500)
    PasswordStrengthLevel.Fair -> Color.Yellow
    PasswordStrengthLevel.Good -> Color.Green
    PasswordStrengthLevel.Strong -> Color(0xFF006400)
    PasswordStrengthLevel.Robust -> Color.Blue
}
