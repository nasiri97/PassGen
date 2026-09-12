package ir.ornix.passgen.core.domain.core

import ir.ornix.passgen.passwordgenerator.core.PassGen
import ir.ornix.passgen.passwordgenerator.model.PassEncoder


interface PassGenConfig {
    val id: Int
    val name: String
    val passEncoder: PassEncoder
    val passwordLength: Int
    val typeBrief: String

    fun createPassGen(): PassGen
}