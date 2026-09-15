package ir.ornix.passgen.core.data

actual object PlatformCrypto {
    actual fun encrypt(data: String): String {
        return data.map { (it.code xor 42).toChar() }.joinToString("")
    }

    actual fun decrypt(data: String): String {
        return data.map { (it.code xor 42).toChar() }.joinToString("")
    }
}
