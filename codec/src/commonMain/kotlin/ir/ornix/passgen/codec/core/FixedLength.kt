package ir.ornix.passgen.codec.core

/**
 * Fixed-length codec: A codec in which each encoding unit has a constant and deterministic size, so the encoded output length can be calculated directly from the input length.
 */
interface FixedLength {

    /**
     * The number of input bytes processed as a single block by the encoding.
     * For example, Base64 processes input in blocks of 3 bytes.
     */
    val blockSize: Int

    /**
     * The number of encoded-units produced for each input block.
     * For example, Base64 produces 4 encoded characters for every 3 input bytes.
     */
    val encodedBlockSize: Int

    /**
     * The number of bits used to represent a single encoded-unit (character/digit) in the encoding.
     * For example, Base64 uses 6 bits per character.
     */
    val bitsPerUnit: Float
        get() = (blockSize * Byte.SIZE_BITS.toFloat()) / encodedBlockSize

    /**
     * Calculates the number of encoded units required to represent
     * an input of the given byte size.
     *
     * The input is processed in [fixed-size blocks][blockSize]. Each block is expanded
     * into bits and then encoded into units of size [bitsPerUnit].
     *
     * The result is rounded up to ensure sufficient capacity for partial units.
     *
     * @param inputByteCount Number of bytes in the original input.
     * @return Number of encoded units required.
     */
    fun encodedUnitCount(inputByteCount: Int): Int {
        val blockCount = (inputByteCount + blockSize - 1) / blockSize
        return blockCount * encodedBlockSize
    }


    /**
     * Calculates a lower-bound (floored) estimate of the number of decoded bytes represented by the given
     * number of encoded units.
     *
     * Encoded units are first converted back into bits using [bitsPerUnit].
     * These bits are then grouped into fixed-size blocks of [blockSize] bytes.
     *
     * Any remaining bits that do not form a complete block are discarded,
     * as they cannot represent additional decoded bytes.
     *
     * @param inputUnitCount Number of encoded units.
     * @return Number of decoded bytes represented.
     */
    fun decodedApproximateByteCount(inputUnitCount: Int): Int {
        val fullBlockCount = inputUnitCount / encodedBlockSize
        return fullBlockCount * blockSize
    }
}