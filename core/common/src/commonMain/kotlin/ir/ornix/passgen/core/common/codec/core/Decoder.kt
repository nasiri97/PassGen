package ir.ornix.passgen.core.common.codec.core

interface Decoder {
    fun decode(input: String): ByteArray
}