package ir.ornix.passgen.passwordgenerator.model

import ir.ornix.passgen.passwordgenerator.model.Password.Companion.toPassword
import kotlinx.serialization.Serializable

/** Post-processing settings for generated passwords (e.g., length). */
@Serializable
data class PostProcessConfig(
    val passwordLength: Int
) {


    /**
     * Finds the most secure password candidate within a token.
     *
     * This function scans the given token and evaluates every contiguous substring
     * of the specified length as a password candidate. Each candidate is converted
     * to a [Password] instance and compared based on its precomputed
     * [Password.strengthScore].
     *
     * The password with the highest security score is selected and returned.
     * If multiple candidates have the same score, the first occurrence is chosen.
     *
     * @param token The source string from which password candidates are extracted.
     * @return The most secure [Password] found, or null if no valid candidate exists.
     */
    operator fun invoke(token: String?): Password? {
        if (token.isNullOrEmpty()) return null
        var mostSecurePassword: Password? = null

        for (passStartIndex in 0..(token.length - passwordLength)) {
            token.substring(passStartIndex, passStartIndex + passwordLength).let {
                val password = it.toPassword()
                if (password.strengthScore > (mostSecurePassword?.strengthScore ?: 0))
                    mostSecurePassword = password
            }
        }
        return mostSecurePassword
    }
}