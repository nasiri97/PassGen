package ir.ornix.passgen.codec.core

interface FixedLengthDecoder : Decoder, FixedLength {

    override fun decode(input: String): ByteArray {
        require(input.length % encodedBlockSize == 0) {
            "Input length (${input.length}) must be a multiple of encoded block size ($encodedBlockSize)"
        }

        return decodeInternal(input)
    }


    fun decodeInternal(input: String): ByteArray
}