package ir.ornix.passgen.feature.config.impl.kdf.presentation

internal const val MIN_MASTER_KEY_LENGTH = 16

internal data class MasterKeyValidation(
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

internal fun validateMasterKey(
    masterKey: CharArray,
    confirmMasterKey: CharArray
): MasterKeyValidation {

    val hasMinLength = masterKey.size >= MIN_MASTER_KEY_LENGTH
    val hasLowercase = masterKey.any { it.isLowerCase() }
    val hasUppercase = masterKey.any { it.isUpperCase() }
    val hasDigit = masterKey.any { it.isDigit() }
    val hasSpecialChar = masterKey.any { !it.isLetterOrDigit() && !it.isWhitespace() }
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
