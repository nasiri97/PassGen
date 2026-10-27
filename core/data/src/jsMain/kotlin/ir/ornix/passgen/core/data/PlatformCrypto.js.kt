package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.Crypto

actual object PlatformCrypto : Crypto {
    actual override fun encrypt(data: ByteArray): ByteArray {
        return data
    }

    actual override fun decrypt(data: ByteArray): ByteArray {
        return data
    }
}
