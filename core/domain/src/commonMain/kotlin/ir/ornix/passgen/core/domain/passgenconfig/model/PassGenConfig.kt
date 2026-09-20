package ir.ornix.passgen.core.domain.passgenconfig.model

import ir.ornix.passgen.core.common.passwordgenerator.core.PassGen
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder


interface PassGenConfig {
    val id: Int
    val name: String
    val passEncoder: PassEncoder
    val passwordLength: Int
    val typeBrief: String

    fun createPassGen(): PassGen
}