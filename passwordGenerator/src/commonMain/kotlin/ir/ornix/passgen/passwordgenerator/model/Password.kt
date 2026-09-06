package ir.ornix.passgen.passwordgenerator.model

data class Password(
    val value: String,
    val hasUpper: Boolean,
    val hasLower: Boolean,
    val hasDigit: Boolean,
    val hasSpecialChar: Boolean
) {

    val length = value.length

    val strengthScore: Int by lazy {
        var score = 0
        if (hasUpper) score++
        if (hasLower) score++
        if (hasDigit) score++
        if (hasSpecialChar) score++

        score += when (length) {
            in 8..15 -> 1
            in 16..Int.MAX_VALUE -> 2
            else -> 0
        }

        score
    }

    val strengthRatio: Float by lazy {
        strengthScore.toFloat() / MAX_STRENGTH_SCORE
    }

    val strengthLevel: PasswordStrengthLevel = when {
        strengthRatio <= (1f / MAX_STRENGTH_SCORE) -> PasswordStrengthLevel.Fragile
        strengthRatio <= (2f / MAX_STRENGTH_SCORE) -> PasswordStrengthLevel.Weak
        strengthRatio <= (3f / MAX_STRENGTH_SCORE) -> PasswordStrengthLevel.Fair
        strengthRatio <= (4f / MAX_STRENGTH_SCORE) -> PasswordStrengthLevel.Good
        strengthRatio <= (5f / MAX_STRENGTH_SCORE) -> PasswordStrengthLevel.Strong
        else -> PasswordStrengthLevel.Robust
    }

    companion object {

        private const val MAX_STRENGTH_SCORE = 6f

        val EmptyPassword = "".toPassword()

        fun String.toPassword(): Password {
            var upper = false
            var lower = false
            var digit = false
            var special = false

            for (c in this) {
                when {
                    c.isUpperCase() -> upper = true
                    c.isLowerCase() -> lower = true
                    c.isDigit() -> digit = true
                    else -> special = true
                }
            }

            return Password(
                value = this,
                hasUpper = upper,
                hasLower = lower,
                hasDigit = digit,
                hasSpecialChar = special
            )
        }
    }
}

enum class PasswordStrengthLevel {
    Fragile, Weak, Fair, Good, Strong, Robust
}

fun Password.strengthLabel(): String = strengthLevel.name
