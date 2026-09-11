package ir.ornix.passgen.passwordgenerator.random

import ir.ornix.passgen.passwordgenerator.core.PassGen

data class RandomPassGen(override val passGenConfig: RandomPassGenConfig) : PassGen {

    fun generate(): String {
        require(passGenConfig.passwordLength >= 4) { "Password length must be at least 4" }
        return passGenConfig.passEncoder.encode(secureRandomBytes(passGenConfig.passwordLength))
    }
}