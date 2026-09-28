package ir.ornix.passgen.core.common.codec.core

interface Decoder {
    suspend fun decode(input: String): ByteArray
}