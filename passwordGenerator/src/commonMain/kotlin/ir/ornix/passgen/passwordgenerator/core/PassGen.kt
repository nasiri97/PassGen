package ir.ornix.passgen.passwordgenerator.core

import ir.ornix.passgen.passwordgenerator.model.PassEncoder

interface PassGen {
    val passEncoder: PassEncoder
    val passwordLength: Int
}