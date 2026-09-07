package ir.ornix.passgen.codec

import ir.ornix.passgen.codec.core.Codec

/**
 * A [Codec] implementation that treats the binary data as UTF-8 encoded
 * text, delegating directly to Kotlin's built-in [ByteArray.decodeToString]
 * and [String.encodeToByteArray].
 *
 * Unlike the other codecs in this package (Base64, BCrypt Base64, Z85,
 * Hex), this one is not a binary-to-text *transformation* — it does not
 * re-represent arbitrary bytes using a restricted alphabet. It simply
 * interprets bytes as UTF-8 text and vice versa, so it is only meaningful
 * when the underlying bytes are (or are meant to be) valid UTF-8 in the
 * first place.
 *
 * ### How it works
 * - [encode] decodes the given bytes as UTF-8 into a [String].
 * - [decode] encodes the given [String] back into UTF-8 bytes.
 *
 * Malformed byte sequences are handled **leniently**, not by throwing:
 * any invalid or incomplete UTF-8 byte sequence passed to [encode] is
 * replaced with the Unicode replacement character (`U+FFFD`, `�`), per
 * the standard behavior of [ByteArray.decodeToString].
 *
 * ### Size Relationship
 * There is no fixed block size or padding scheme — every byte array
 * length is valid input, and the resulting string length depends entirely
 * on how many bytes each character occupies in UTF-8 (1 to 4 bytes per
 * character). This differs fundamentally from the other codecs, where
 * output length is a deterministic function of input length; here it is
 * data-dependent.
 */
class Utf8TextCodec : Codec {

    override fun decode(input: String): ByteArray {
        return input.encodeToByteArray()
    }

    override fun encode(input: ByteArray): String {
        return input.decodeToString()
    }
}