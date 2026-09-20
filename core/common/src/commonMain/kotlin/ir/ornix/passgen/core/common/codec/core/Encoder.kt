package ir.ornix.passgen.core.common.codec.core

interface Encoder {
    fun encode(input: ByteArray): String
}