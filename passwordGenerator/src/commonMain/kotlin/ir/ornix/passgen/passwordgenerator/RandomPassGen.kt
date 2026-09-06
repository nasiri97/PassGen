package ir.ornix.passgen.passwordgenerator

import ir.ornix.passgen.passwordgenerator.model.Password
import ir.ornix.passgen.passwordgenerator.model.Password.Companion.toPassword
import ir.ornix.passgen.passwordgenerator.model.RandomPassGenConfig
import kotlin.random.Random

data class RandomPassGen(override val passGenConfig: RandomPassGenConfig) : PassGen {

    companion object {
        private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
        private const val DIGITS = "0123456789"
        private const val SPECIALS = "!@#\\$%^&*()-_=+[]{};:,.<>?/"
        private const val ALL = UPPERCASE + LOWERCASE + DIGITS + SPECIALS
    }

    fun generate(): Password {
        require(passGenConfig.passwordLength >= 4) { "Password length must be at least 4" }

        val mandatoryChars = listOf(
            UPPERCASE.random(),
            LOWERCASE.random(),
            DIGITS.random(),
            SPECIALS.random()
        )

        val remaining = (4 until passGenConfig.passwordLength).map { ALL.random() }
        val passwordChars = (mandatoryChars + remaining).shuffled(Random)
        return passwordChars.joinToString("").toPassword()
    }
}