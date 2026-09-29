package ir.ornix.passgen.core.model

import ir.ornix.passgen.core.model.PasswordStrengthLevel.Companion.entropyByteSizeToPasswordStrengthLevel

data class Password(
    val value: String,
    val entropyByteSize: Int
) {

    val length = value.length

    val strengthLevel: PasswordStrengthLevel =
        entropyByteSizeToPasswordStrengthLevel(entropyByteSize)


    var hasUpper: Boolean = false
        private set
    var hasLower: Boolean = false
        private set
    var hasDigit: Boolean = false
        private set
    var hasSpecialChar: Boolean = false
        private set

    init {
        for (c in value) {
            when {
                c.isUpperCase() -> hasUpper = true
                c.isLowerCase() -> hasLower = true
                c.isDigit() -> hasDigit = true
                else -> hasSpecialChar = true
            }
        }
    }

    companion object {
        fun String.toPassword(entropyByteSize: Int): Password {
            return Password(
                value = this,
                entropyByteSize = entropyByteSize
            )
        }
    }
}
