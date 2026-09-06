package ir.ornix.passgen.passwordgenerator.model

import kotlinx.serialization.Serializable

/**
 * Configuration for preprocessing text before further processing
 */
@Serializable
data class PreprocessConfig(
    /** Trim spaces at the beginning and end of the text */
    val trimLeadingAndTrailingSpaces: Boolean,

    /** Replace multiple consecutive spaces with a single space */
    val collapseMultipleSpaces: Boolean,

    /** Convert all characters to lowercase */
    val convertToLowercase: Boolean
) {

    companion object {
        const val SINGLE_SPACE = ' '
    }

    operator fun invoke(input: String): String {
        var normalizedInput = input
        if (convertToLowercase)
            normalizedInput = normalizedInput.convertToLowercase()
        if (collapseMultipleSpaces)
            normalizedInput = normalizedInput.collapseMultipleSpaces()
        if (trimLeadingAndTrailingSpaces)
            normalizedInput = normalizedInput.trimLeadingAndTrailingSpaces()

        return normalizedInput
    }

    /** Converts characters to lowercase using locale‑independent rules (safe for English alphabet). */
    private fun String.convertToLowercase() =
        this.lowercase()

    /** Trims leading and trailing ASCII control characters and spaces (code points ≤ U+0020) */
    private fun String.trimLeadingAndTrailingSpaces() =
        this.trim { it <= ' ' }

    /** Replacing multiple consecutive spaces with a single space */
    private fun String.collapseMultipleSpaces() =
        this.replace("$SINGLE_SPACE+".toRegex(), "$SINGLE_SPACE")
}