package ir.ornix.passgen.core.domain

enum class LocalAuthType(val value: String) {
    NONE("None"),
    PIN("PIN"),
    PASSWORD("Password"),
    PATTERN("Pattern")
}

