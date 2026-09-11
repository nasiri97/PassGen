package ir.ornix.passgen.codec


import ir.ornix.passgen.codec.core.Codec
import kotlin.io.encoding.Base64


/**
 * A [Codec] implementation backed by the standard RFC 4648 Base64 alphabet
 * (`A-Za-z0-9+/`), delegating the actual encoding/decoding work to Kotlin's
 * built-in [kotlin.io.encoding.Base64].
 *
 * Unlike [BCryptBase64BinaryCodec], this codec follows the standard and uses
 * `=` padding to keep the encoded output length a clean multiple of 4.
 *
 * ### How it works
 * Input bytes are processed in groups of 3 (24 bits), split into four 6-bit
 * groups, each mapped to one character of the alphabet — so a full 3-byte
 * block always becomes exactly 4 characters. The input does **not** need to
 * be a multiple of 3 in length; only the *last* group may be partial:
 *
 * | Bytes in final group | Output chars | Padding |
 * |---|---|---|
 * | 3 (full block)         | 4 | none |
 * | 2                      | 4 | 1 `=` |
 * | 1                      | 4 | 2 `==` |
 *
 * This means the overall encoded length always follows `ceil(n / 3) * 4`,
 * where `n` is the input byte count — the padding exists precisely to make
 * up the difference for a trailing partial group, not because the input
 * itself must be a multiple of 3.
 *
 *
 * ### Size Relationship (3 bytes → 4 characters)
 *
 * Every string produced by [encode] has a length that's a multiple of 4,
 * no matter what the original input length was. That's because encoding
 * always processes bytes in groups of 3 and always emits exactly 4
 * characters per group — using '=' padding to fill out a partial trailing
 * group rather than emitting a shorter, unpadded chunk.
 *
 * So:
 * - Input byte count                   -> can be any length (0, 1, 2, 3, 4, 5, ...)
 * - [encode] output length             -> always a multiple of 4 (0, 4, 8, 12, ...)
 * - Output length if `=` is omitted    -> may fall short of a multiple of 4 for a partial trailing group
 *
 * The '=' padding exists specifically to guarantee that second property
 * for output produced by this codec. Note, however, that [decode] is
 * configured with [Base64.PaddingOption.PRESENT_OPTIONAL], so it will also
 * accept *unpadded* input whose length is not a multiple of 4 — `=` is
 * required when [encode] emits output, but optional/may be omitted when
 * [decode] is given input.
 *
 * @throws IllegalArgumentException if [decode] is given a malformed Base64
 * string (invalid characters, incorrect padding, or a length not congruent
 * with the encoding rules above).
 */
class Base64BinaryCodec : Codec {

    override fun decode(input: String): ByteArray {
        return Base64.withPadding(Base64.PaddingOption.PRESENT_OPTIONAL).decode(input)
    }

    override fun encode(input: ByteArray): String {
        return Base64.encode(input)
    }
}