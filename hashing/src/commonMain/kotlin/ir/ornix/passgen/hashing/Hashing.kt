package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.core.Decoder
import ir.ornix.passgen.codec.core.Encoder

interface Hashing {

    val outputByteSize: Int

    suspend fun digest(input: ByteArray): ByteArray

    suspend fun digest(
        input: String,
        inputDecoder: Decoder
    ): ByteArray {
        return digest(inputDecoder.decode(input))
    }

    suspend fun digest(
        input: ByteArray,
        outputEncoder: Encoder
    ): String {
        val result = digest(input)
        return outputEncoder.encode(result)
    }

    suspend fun digest(
        input: String,
        inputDecoder: Decoder,
        outputEncoder: Encoder
    ): String {
        val result = digest(input, inputDecoder)
        return outputEncoder.encode(result)
    }

    suspend fun verify(input: ByteArray, digest: ByteArray): Boolean {
        return digest(input).contentEquals(digest)
    }
}