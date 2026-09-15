package ir.ornix.passgen.core.data

expect object PlatformCrypto {
    fun encrypt(data: String): String
    fun decrypt(data: String): String
}
