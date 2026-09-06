package ir.ornix.passgen.passwordgenerator.model

interface PassGenConfig {

    val id: Int
    val name: String
    val passwordLength: Int
    val typeBrief: String
}