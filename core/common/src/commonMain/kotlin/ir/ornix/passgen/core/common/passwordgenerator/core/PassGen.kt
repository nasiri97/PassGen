package ir.ornix.passgen.core.common.passwordgenerator.core

import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder

interface PassGen {
    val passEncoder: PassEncoder
    val passwordLength: Int?
}