package ir.ornix.passgen.passwordgenerator.kdf

data class PassGenFeed(
    val masterKey: String,
    val input: String
)