package ir.ornix.passgen.codec.core

interface Decoder {
    fun decode(input: String): ByteArray
}