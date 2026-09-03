package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.Base64BinaryCodec
import ir.ornix.passgen.codec.HexBinaryCodec
import ir.ornix.passgen.codec.Utf8TextCodec
import ir.ornix.passgen.codec.Z85BinaryCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Argon2HashingTest {

    companion object {
        private val base64Codec = Base64BinaryCodec()
        private val z85BinaryCodec = Z85BinaryCodec()
        private val utf8TextCodec = Utf8TextCodec()
        private val hexBinaryCodec = HexBinaryCodec(false)

        private const val STR1 = ""
        private val STR1_BYTES = utf8TextCodec.decode(STR1)
        private const val STR1_SHA256 =
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        private val SALT1 = hexBinaryCodec.decode(STR1_SHA256.substring(0, 36))
        private const val ARGON1 =
            $$"$argon2id$v=19$m=131072,t=4,p=1$47DEQpj8HBSa+/TImW+5JCeu$TZEUFRjR4XglkuGWigLExSHWkHeZPgGbZbTzc3GO8v+0s0Q2bUdKA5dIvT8enyLMEioyKap7V8RtFOPWe2duySNPTiXYLZoe"
        private val DIGEST1_BASE64 = ARGON1.substring(ARGON1.lastIndexOf('$') + 1)
        private val DIGEST1 = base64Codec.decode(DIGEST1_BASE64)

        private const val STR2 = "\n\n  "
        private val STR2_BYTES = utf8TextCodec.decode(STR2)
        private const val STR2_SHA256 =
            "4308ef96ad0e86f35c795a177206056556333e814e65bfc9cd04bb164f8d61eb"
        private val SALT2 = hexBinaryCodec.decode(STR2_SHA256.substring(0, 36))
        private const val ARGON2 =
            $$"$argon2id$v=19$m=131072,t=4,p=1$Qwjvlq0OhvNceVoXcgYFZVYz$jxc0uewPv9Won/r+sPNVczuSzFgEUjta5Sl7OjqvOz/7m+jkaPStsIF3WFMS5s7PVnyL+ohUwU8Ziqig4w6W8M9DCM5D7yI9"
        private val DIGEST2_BASE64 = ARGON2.substring(ARGON2.lastIndexOf('$') + 1)
        private val DIGEST2 = base64Codec.decode(DIGEST2_BASE64)

        private const val STR3 = "hello"
        private val STR3_BYTES = utf8TextCodec.decode(STR3)
        private const val STR3_SHA256 =
            "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
        private val SALT3 = hexBinaryCodec.decode(STR3_SHA256.substring(0, 36))
        private const val ARGON3 =
            $$"$argon2id$v=19$m=131072,t=4,p=1$LPJNul+wow4m6DsqxbninhsW$fWJxxBMutLK9qCj/WsMCGQXjl2Qq9NeJtWge4K1pSHw/HOwouo0f7h5DAk/1nHim98UsH2JFESoALSiUCKszX2w41v/2nUX3"
        private val DIGEST3_BASE64 = ARGON3.substring(ARGON3.lastIndexOf('$') + 1)
        private val DIGEST3 = base64Codec.decode(DIGEST3_BASE64)

        private const val STR4 = "Hello World"
        private val STR4_BYTES = utf8TextCodec.decode(STR4)
        private const val STR4_SHA256 =
            "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e"
        private val SALT4 = hexBinaryCodec.decode(STR4_SHA256.substring(0, 36))
        private const val ARGON4 =
            $$"$argon2id$v=19$m=131072,t=4,p=1$pZGm1Av0IEBKARczz7exkNYs$Foy0cX290UvEGXtJ7ZgHilwaKDRCietAg0QgYE+oTU+/qjWSOV9zsE/ScdADXCfzQWty+zay4A7bIROCcQgnE0ua1s309wZ1"
        private val DIGEST4_BASE64 = ARGON4.substring(ARGON4.lastIndexOf('$') + 1)
        private val DIGEST4 = base64Codec.decode(DIGEST4_BASE64)

        private const val STR5 = "This is a simple tes! We are using Argon2_id.\nHave a good time."
        private val STR5_BYTES = utf8TextCodec.decode(STR5)
        private const val STR5_SHA256 =
            "ecbb28cc36276854bf5f7153b9d5eb6c07f65f50d49a50f4578fb2665e43d665"
        private val SALT5 = hexBinaryCodec.decode(STR5_SHA256.substring(0, 36))
        private const val ARGON5 =
            $$"$argon2id$v=19$m=131072,t=4,p=1$7LsozDYnaFS/X3FTudXrbAf2$DqjtViwkPRwtfKjclhgr7fdCR159bxdZOJtkDHP/7pPpnlWeQQyFrjTx+Pb0wIiPGUjrF49X4OCFfsuc6zgu/XRRNI+MywtR"
        private val DIGEST5_BASE64 = ARGON5.substring(ARGON5.lastIndexOf('$') + 1)
        private val DIGEST5 = base64Codec.decode(DIGEST5_BASE64)
    }

    @Test
    fun testSaltLength() = runTest {
        // Salt size should be 18 bytes
        assertEquals(18, SALT1.size)
        assertEquals(18, SALT2.size)
        assertEquals(18, SALT3.size)
        assertEquals(18, SALT4.size)
        assertEquals(18, SALT5.size)
    }

    @Test
    fun testCustomSalt() = runTest {
        // Getting digest with custom salt, should return the same digest
        assertContentEquals(DIGEST1, Argon2Hashing.digest(STR1_BYTES, SALT1))
        assertContentEquals(DIGEST2, Argon2Hashing.digest(STR2_BYTES, SALT2))
        assertContentEquals(DIGEST3, Argon2Hashing.digest(STR3_BYTES, SALT3))
        assertContentEquals(DIGEST4, Argon2Hashing.digest(STR4_BYTES, SALT4))
        assertContentEquals(DIGEST5, Argon2Hashing.digest(STR5_BYTES, SALT5))
    }

    @Test
    fun testArgonBase64Length() {
        assertEquals(153, ARGON1.length)
        assertEquals(153, ARGON2.length)
        assertEquals(153, ARGON3.length)
        assertEquals(153, ARGON4.length)
        assertEquals(153, ARGON5.length)
    }

    @Test
    fun testDigest() = runTest {
        // Bytes
        assertContentEquals(DIGEST1, Argon2Hashing.digest(STR1_BYTES))
        assertContentEquals(DIGEST2, Argon2Hashing.digest(STR2_BYTES))
        assertContentEquals(DIGEST3, Argon2Hashing.digest(STR3_BYTES))
        assertContentEquals(DIGEST4, Argon2Hashing.digest(STR4_BYTES))
        assertContentEquals(DIGEST5, Argon2Hashing.digest(STR5_BYTES))

        // Base64
        assertEquals(DIGEST1_BASE64, Argon2Hashing.digest(STR1_BYTES, base64Codec))
        assertEquals(DIGEST2_BASE64, Argon2Hashing.digest(STR2_BYTES, base64Codec))
        assertEquals(DIGEST3_BASE64, Argon2Hashing.digest(STR3_BYTES, base64Codec))
        assertEquals(DIGEST4_BASE64, Argon2Hashing.digest(STR4_BYTES, base64Codec))
        assertEquals(DIGEST5_BASE64, Argon2Hashing.digest(STR5_BYTES, base64Codec))
    }


    @Test
    fun testDigestLength() = runTest {
        // 72 bytes
        assertEquals(72, Argon2Hashing.digest(STR1_BYTES).size)
        assertEquals(72, Argon2Hashing.digest(STR2_BYTES).size)
        assertEquals(72, Argon2Hashing.digest(STR3_BYTES).size)
        assertEquals(72, Argon2Hashing.digest(STR4_BYTES).size)
        assertEquals(72, Argon2Hashing.digest(STR5_BYTES).size)

        // 144 Hex-Chars
        assertEquals(144, Argon2Hashing.digest(STR1_BYTES, hexBinaryCodec).length)
        assertEquals(144, Argon2Hashing.digest(STR2_BYTES, hexBinaryCodec).length)
        assertEquals(144, Argon2Hashing.digest(STR3_BYTES, hexBinaryCodec).length)
        assertEquals(144, Argon2Hashing.digest(STR4_BYTES, hexBinaryCodec).length)
        assertEquals(144, Argon2Hashing.digest(STR5_BYTES, hexBinaryCodec).length)

        // 96 Base64-Chars
        assertEquals(96, Argon2Hashing.digest(STR1_BYTES, base64Codec).length)
        assertEquals(96, Argon2Hashing.digest(STR2_BYTES, base64Codec).length)
        assertEquals(96, Argon2Hashing.digest(STR3_BYTES, base64Codec).length)
        assertEquals(96, Argon2Hashing.digest(STR4_BYTES, base64Codec).length)
        assertEquals(96, Argon2Hashing.digest(STR5_BYTES, base64Codec).length)

        // 90 Z85-Chars
        assertEquals(90, Argon2Hashing.digest(STR1_BYTES, z85BinaryCodec).length)
        assertEquals(90, Argon2Hashing.digest(STR2_BYTES, z85BinaryCodec).length)
        assertEquals(90, Argon2Hashing.digest(STR3_BYTES, z85BinaryCodec).length)
        assertEquals(90, Argon2Hashing.digest(STR4_BYTES, z85BinaryCodec).length)
        assertEquals(90, Argon2Hashing.digest(STR5_BYTES, z85BinaryCodec).length)
    }
}