package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.Crypto

expect object PlatformCrypto : Crypto {
    override fun encrypt(data: ByteArray): ByteArray
    override fun decrypt(data: ByteArray): ByteArray
}
