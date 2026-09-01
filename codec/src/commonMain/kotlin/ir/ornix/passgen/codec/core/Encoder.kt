package ir.ornix.passgen.codec.core

interface Encoder {
    fun encode(input: ByteArray): String
}