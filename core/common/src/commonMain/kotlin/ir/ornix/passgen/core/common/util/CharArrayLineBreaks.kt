package ir.ornix.passgen.core.common.util

// Zero-Width Space: invisible, but lets the text layout break a line at this position
private const val ZWSP = '\u200B'

/**
 * Returns a new array with a zero-width space (`U+200B`) between every character, so a
 * text layout can wrap the content at any character instead of only at spaces or symbols.
 * This is meant for display only (for example, wrapping a password in a `Text`).
 *
 * If the array contains a space (`' '`), it already wraps naturally, so a copy with the
 * same content is returned and no zero-width spaces are inserted.
 *
 * **Memory and security**
 * - The receiver is never modified.
 * - The result is always a new array, never the receiver itself, even when nothing is inserted.
 * - The caller owns the returned array and must wipe it with `fill('\u0000')` as soon as
 *   it is no longer needed. This function does not wipe it.
 * - Never copy or store the returned array as the real value: it may contain invisible
 *   characters. Use the original array for copying, hashing, or any other processing.
 *
 * Characters are processed as UTF-16 code units, so a surrogate pair (such as an emoji)
 * can be split. This is safe for ASCII and BMP-only content.
 *
 * @return A new array of size `2 * size - 1` with a zero-width space between each pair of
 * characters, or a same-size copy if the receiver contains a space. An empty receiver
 * returns an empty array.
 */
fun CharArray.withLineBreakOpportunities(): CharArray {
    if (isEmpty()) return CharArray(0)
    if (contains(' ')) return copyOf()

    val out = CharArray(size * 2 - 1)
    for (i in indices) {
        out[i * 2] = this[i]
        if (i < lastIndex) out[i * 2 + 1] = ZWSP
    }
    return out
}