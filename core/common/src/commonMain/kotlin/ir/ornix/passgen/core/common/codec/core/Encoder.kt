package ir.ornix.passgen.core.common.codec.core

interface Encoder {
    suspend fun encode(input: ByteArray): String
}