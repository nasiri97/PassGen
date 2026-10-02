package ir.ornix.passgen.core.model

import ir.ornix.passgen.core.model.PasswordStrengthLevel.Companion.entropyByteSizeToPasswordStrengthLevel
import kotlinx.serialization.Serializable

@Serializable
data class Password(
    val value: CharArray,
    val entropyByteSize: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Password) return false

        return value.contentEquals(other.value) &&
                entropyByteSize == other.entropyByteSize
    }

    override fun hashCode(): Int {
        var result = value.contentHashCode()
        result = 31 * result + entropyByteSize
        return result
    }

    val length = value.size

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
                value = this.toCharArray(),
                entropyByteSize = entropyByteSize
            )
        }
    }
}
