package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.Base64BinaryCodec
import ir.ornix.passgen.codec.HexBinaryCodec
import ir.ornix.passgen.codec.Utf8TextCodec
import ir.ornix.passgen.codec.Z85BinaryCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Argon2IdHasherTest {

    companion object {
        private val base64Codec = Base64BinaryCodec()
        private val z85BinaryCodec = Z85BinaryCodec()
        private val utf8TextCodec = Utf8TextCodec()
        private val hexBinaryCodec = HexBinaryCodec(false)

        private const val STR1 = ""
        private val STR1_BYTES = utf8TextCodec.decode(STR1)
        private const val STR1_SHA256 =
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        private val SALT1 = hexBinaryCodec.decode(STR1_SHA256.substring(0, 32))

        private const val PHC_ENCODED1 = // Salt: 47DEQpj8HBSa+/TImW+5JA==
            $$"$argon2id$v=19$m=131072,t=4,p=1$47DEQpj8HBSa+/TImW+5JA$SIccW0Qtg/lQJyg+5S5HaSe5hzUqpzlpT0FZHH24rpPHsvua5MjBldLxXVCGYztmsJtLiU7D7QZWzN/mlNKkvQ"
        private val DIGEST1_BASE64 = PHC_ENCODED1.substring(PHC_ENCODED1.lastIndexOf('$') + 1)
        private val DIGEST1 = base64Codec.decode(DIGEST1_BASE64)

        private const val STR2 = " "
        private val STR2_BYTES = utf8TextCodec.decode(STR2)
        private const val STR2_SHA256 =
            "36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068"
        private val SALT2 = hexBinaryCodec.decode(STR2_SHA256.substring(0, 32))
        private const val PHC_ENCODED2 = // Salt: Nqnn8clbgv+5l0PgxcTOlQ==
            $$"$argon2id$v=19$m=131072,t=4,p=1$Nqnn8clbgv+5l0PgxcTOlQ$1vjyDAtqCH+KQPxO0mhBR4zbIoF2919kBVnLuECm8M8Kq+3vc9oyLS6InLO2whmP0CCgTcNd83yKBqZTfNpzYw"
        private val DIGEST2_BASE64 = PHC_ENCODED2.substring(PHC_ENCODED2.lastIndexOf('$') + 1)
        private val DIGEST2 = base64Codec.decode(DIGEST2_BASE64)

        private const val STR3 = "\n\n  "
        private val STR3_BYTES = utf8TextCodec.decode(STR3)
        private const val STR3_SHA256 =
            "4308ef96ad0e86f35c795a177206056556333e814e65bfc9cd04bb164f8d61eb"
        private val SALT3 = hexBinaryCodec.decode(STR3_SHA256.substring(0, 32))
        private const val PHC_ENCODED3 = //Salt: Qwjvlq0OhvNceVoXcgYFZQ==
            $$"$argon2id$v=19$m=131072,t=4,p=1$Qwjvlq0OhvNceVoXcgYFZQ$jNwuBTBCAMjy3anuNFnZZqwhBB6BNpF+1BFFMX8xpVIzXVdPSOEp0/3nR00KuzAJivSpCGORKRlotWhqvnLVbw"
        private val DIGEST3_BASE64 = PHC_ENCODED3.substring(PHC_ENCODED3.lastIndexOf('$') + 1)
        private val DIGEST3 = base64Codec.decode(DIGEST3_BASE64)

        private const val STR4 = "hello"
        private val STR4_BYTES = utf8TextCodec.decode(STR4)
        private const val STR4_SHA256 =
            "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
        private val SALT4 = hexBinaryCodec.decode(STR4_SHA256.substring(0, 32))
        private const val PHC_ENCODED4 = // Salt: LPJNul+wow4m6Dsqxbning==
            $$"$argon2id$v=19$m=131072,t=4,p=1$LPJNul+wow4m6Dsqxbning$Jomso/aWbL3bXqb0Y+WuQOdMiveFYCBBC9FClw5pAihWocz0xFhc+Qns2PJZ3PhBp8doN1Eb9dyx36q1B50lBg"
        private val DIGEST4_BASE64 = PHC_ENCODED4.substring(PHC_ENCODED4.lastIndexOf('$') + 1)
        private val DIGEST4 = base64Codec.decode(DIGEST4_BASE64)

        private const val STR5 = "Hello World"
        private val STR5_BYTES = utf8TextCodec.decode(STR5)
        private const val STR5_SHA256 =
            "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e"
        private val SALT5 = hexBinaryCodec.decode(STR5_SHA256.substring(0, 32))
        private const val PHC_ENCODED5 = // Salt: pZGm1Av0IEBKARczz7exkA==
            $$"$argon2id$v=19$m=131072,t=4,p=1$pZGm1Av0IEBKARczz7exkA$whbMNRoF5/S71AQakvFTqefPzAnppbAGVdGk3fKf9qselK/JGaY5fDawfqfVQ6hkTVWnMVoHzN7GiuwDz9a6/A"
        private val DIGEST5_BASE64 = PHC_ENCODED5.substring(PHC_ENCODED5.lastIndexOf('$') + 1)
        private val DIGEST5 = base64Codec.decode(DIGEST5_BASE64)

        private const val STR6 = "This is a simple test! We are using Argon2_id.\nHave a good time."
        private val STR6_BYTES = utf8TextCodec.decode(STR6)
        private const val STR6_SHA256 =
            "6163f9727d2b2f770f94047405ceff3cda92383a94daef53b89641eb817dfd40"
        private val SALT6 = hexBinaryCodec.decode(STR6_SHA256.substring(0, 32))
        private const val PHC_ENCODED6 = // Salt: YWP5cn0rL3cPlAR0Bc7/PA==
            $$"$argon2id$v=19$m=131072,t=4,p=1$YWP5cn0rL3cPlAR0Bc7/PA$MJc2LdHkKAyYx8JHXC676Hrqts8Df80wSI5GXmyLdo4b5WHyGxFKesjFFw2uOGRtpzs7kmBOoxhaJffe7wkwuQ"
        private val DIGEST6_BASE64 = PHC_ENCODED6.substring(PHC_ENCODED6.lastIndexOf('$') + 1)
        private val DIGEST6 = base64Codec.decode(DIGEST6_BASE64)
    }


    @Test
    fun testSalt() = runTest {
        val saltSize = 16

        // Salt size should be 16 bytes
        assertEquals(saltSize, SALT1.size)
        assertEquals(saltSize, SALT2.size)
        assertEquals(saltSize, SALT3.size)
        assertEquals(saltSize, SALT4.size)
        assertEquals(saltSize, SALT5.size)
        assertEquals(saltSize, SALT6.size)

        val salt1 =
            PHC_ENCODED1.substring(PHC_ENCODED1.indexOf('$', 15) + 1, PHC_ENCODED1.lastIndexOf('$'))
        val salt2 =
            PHC_ENCODED2.substring(PHC_ENCODED2.indexOf('$', 15) + 1, PHC_ENCODED2.lastIndexOf('$'))
        val salt3 =
            PHC_ENCODED3.substring(PHC_ENCODED3.indexOf('$', 15) + 1, PHC_ENCODED3.lastIndexOf('$'))
        val salt4 =
            PHC_ENCODED4.substring(PHC_ENCODED4.indexOf('$', 15) + 1, PHC_ENCODED4.lastIndexOf('$'))
        val salt5 =
            PHC_ENCODED5.substring(PHC_ENCODED5.indexOf('$', 15) + 1, PHC_ENCODED5.lastIndexOf('$'))
        val salt6 =
            PHC_ENCODED6.substring(PHC_ENCODED6.indexOf('$', 15) + 1, PHC_ENCODED6.lastIndexOf('$'))

        // 22 Base64-Chars
        assertEquals(22, salt1.length)
        assertEquals(22, salt2.length)
        assertEquals(22, salt3.length)
        assertEquals(22, salt4.length)
        assertEquals(22, salt5.length)
        assertEquals(22, salt6.length)

        assertContentEquals(SALT1, base64Codec.decode(salt1))
        assertContentEquals(SALT2, base64Codec.decode(salt2))
        assertContentEquals(SALT3, base64Codec.decode(salt3))
        assertContentEquals(SALT4, base64Codec.decode(salt4))
        assertContentEquals(SALT5, base64Codec.decode(salt5))
        assertContentEquals(SALT6, base64Codec.decode(salt6))
    }

    @Test
    fun testCustomSalt() = runTest {
        // Getting digest with custom salt, should return the same digest
        assertContentEquals(DIGEST1, Argon2IdHasher.digest(STR1_BYTES, SALT1))
        assertContentEquals(DIGEST2, Argon2IdHasher.digest(STR2_BYTES, SALT2))
        assertContentEquals(DIGEST3, Argon2IdHasher.digest(STR3_BYTES, SALT3))
        assertContentEquals(DIGEST4, Argon2IdHasher.digest(STR4_BYTES, SALT4))
        assertContentEquals(DIGEST5, Argon2IdHasher.digest(STR5_BYTES, SALT5))
        assertContentEquals(DIGEST6, Argon2IdHasher.digest(STR6_BYTES, SALT6))
    }

    @Test
    fun testArgonBase64Length() {
        assertEquals(141, PHC_ENCODED1.length)
        assertEquals(141, PHC_ENCODED2.length)
        assertEquals(141, PHC_ENCODED3.length)
        assertEquals(141, PHC_ENCODED4.length)
        assertEquals(141, PHC_ENCODED5.length)
        assertEquals(141, PHC_ENCODED6.length)
    }

    @Test
    fun testDigest() = runTest {
        // Bytes
        assertContentEquals(DIGEST1, Argon2IdHasher.digest(STR1_BYTES))
        assertContentEquals(DIGEST2, Argon2IdHasher.digest(STR2_BYTES))
        assertContentEquals(DIGEST3, Argon2IdHasher.digest(STR3_BYTES))
        assertContentEquals(DIGEST4, Argon2IdHasher.digest(STR4_BYTES))
        assertContentEquals(DIGEST5, Argon2IdHasher.digest(STR5_BYTES))
        assertContentEquals(DIGEST6, Argon2IdHasher.digest(STR6_BYTES))

        // Base64
        assertEquals("$DIGEST1_BASE64==", Argon2IdHasher.digest(STR1_BYTES, base64Codec))
        assertEquals("$DIGEST2_BASE64==", Argon2IdHasher.digest(STR2_BYTES, base64Codec))
        assertEquals("$DIGEST3_BASE64==", Argon2IdHasher.digest(STR3_BYTES, base64Codec))
        assertEquals("$DIGEST4_BASE64==", Argon2IdHasher.digest(STR4_BYTES, base64Codec))
        assertEquals("$DIGEST5_BASE64==", Argon2IdHasher.digest(STR5_BYTES, base64Codec))
        assertEquals("$DIGEST6_BASE64==", Argon2IdHasher.digest(STR6_BYTES, base64Codec))
    }


    @Test
    fun testDigestLength() = runTest {
        // 72 bytes
        assertEquals(64, Argon2IdHasher.digest(STR1_BYTES).size)
        assertEquals(64, Argon2IdHasher.digest(STR2_BYTES).size)
        assertEquals(64, Argon2IdHasher.digest(STR3_BYTES).size)
        assertEquals(64, Argon2IdHasher.digest(STR4_BYTES).size)
        assertEquals(64, Argon2IdHasher.digest(STR5_BYTES).size)
        assertEquals(64, Argon2IdHasher.digest(STR6_BYTES).size)

        // 144 Hex-Chars
        assertEquals(128, Argon2IdHasher.digest(STR1_BYTES, hexBinaryCodec).length)
        assertEquals(128, Argon2IdHasher.digest(STR2_BYTES, hexBinaryCodec).length)
        assertEquals(128, Argon2IdHasher.digest(STR3_BYTES, hexBinaryCodec).length)
        assertEquals(128, Argon2IdHasher.digest(STR4_BYTES, hexBinaryCodec).length)
        assertEquals(128, Argon2IdHasher.digest(STR5_BYTES, hexBinaryCodec).length)
        assertEquals(128, Argon2IdHasher.digest(STR6_BYTES, hexBinaryCodec).length)

        // 88 Base64-Chars
        assertEquals(88, Argon2IdHasher.digest(STR1_BYTES, base64Codec).length)
        assertEquals(88, Argon2IdHasher.digest(STR2_BYTES, base64Codec).length)
        assertEquals(88, Argon2IdHasher.digest(STR3_BYTES, base64Codec).length)
        assertEquals(88, Argon2IdHasher.digest(STR4_BYTES, base64Codec).length)
        assertEquals(88, Argon2IdHasher.digest(STR5_BYTES, base64Codec).length)
        assertEquals(88, Argon2IdHasher.digest(STR6_BYTES, base64Codec).length)

        // 80 Z85-Chars
        assertEquals(80, Argon2IdHasher.digest(STR1_BYTES, z85BinaryCodec).length)
        assertEquals(80, Argon2IdHasher.digest(STR2_BYTES, z85BinaryCodec).length)
        assertEquals(80, Argon2IdHasher.digest(STR3_BYTES, z85BinaryCodec).length)
        assertEquals(80, Argon2IdHasher.digest(STR4_BYTES, z85BinaryCodec).length)
        assertEquals(80, Argon2IdHasher.digest(STR5_BYTES, z85BinaryCodec).length)
        assertEquals(80, Argon2IdHasher.digest(STR6_BYTES, z85BinaryCodec).length)
    }
}