package ir.ornix.passgen.codec.core

interface FixedLengthEncoder : Encoder, FixedLength {

    override fun encode(input: ByteArray): String {
        require(input.size % blockSize == 0) {
            "Input size (${input.size}) must be a multiple of block size ($blockSize)"
        }

        return encodeInternal(input)
    }

    fun encodeInternal(input: ByteArray): String
}