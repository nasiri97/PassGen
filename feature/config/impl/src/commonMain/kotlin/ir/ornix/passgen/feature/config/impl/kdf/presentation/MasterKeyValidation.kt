package ir.ornix.passgen.feature.config.impl.kdf.presentation

internal const val MIN_MASTER_KEY_LENGTH = 16

data class MasterKeyValidation(
    val hasMinLength: Boolean = false,
    val hasLowercase: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasDigit: Boolean = false,
    val hasSpecialChar: Boolean = false,
    val keysMatch: Boolean = false,
    val masterKeyLength: Int = 0,
    val confirmMasterKeyLength: Int = 0
) {
    val isValid: Boolean
        get() = hasMinLength && hasLowercase && hasUppercase && hasDigit && hasSpecialChar && keysMatch

    val shouldShowConfirmError
        get() = (confirmMasterKeyLength != 0) && !keysMatch
}

fun validateMasterKey(masterKey: ByteArray, confirmMasterKey: ByteArray): MasterKeyValidation {
    val mkStr = masterKey.decodeToString()

    val hasMinLength = masterKey.size >= MIN_MASTER_KEY_LENGTH
    val hasLowercase = mkStr.any { it.isLowerCase() }
    val hasUppercase = mkStr.any { it.isUpperCase() }
    val hasDigit = mkStr.any { it.isDigit() }
    val hasSpecialChar = mkStr.any { !it.isLetterOrDigit() && !it.isWhitespace() }
    val keysMatch = masterKey.isNotEmpty() && masterKey.contentEquals(confirmMasterKey)

    return MasterKeyValidation(
        hasMinLength = hasMinLength,
        hasLowercase = hasLowercase,
        hasUppercase = hasUppercase,
        hasDigit = hasDigit,
        hasSpecialChar = hasSpecialChar,
        keysMatch = keysMatch,
        masterKeyLength = masterKey.size,
        confirmMasterKeyLength = confirmMasterKey.size
    )
}
