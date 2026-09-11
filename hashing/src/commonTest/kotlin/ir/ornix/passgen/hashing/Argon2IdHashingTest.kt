package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.Base64BinaryCodec
import ir.ornix.passgen.codec.HexBinaryCodec
import ir.ornix.passgen.codec.Utf8TextCodec
import ir.ornix.passgen.codec.Z85BinaryCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class Argon2IdHashingTest {

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

        private const val ARGON1 = // Salt: 47DEQpj8HBSa+/TImW+5JA==
            $$"$argon2id$v=19$m=131072,t=4,p=1$47DEQpj8HBSa+/TImW+5JA$7nCeECf0DBRzNoB9W17t0fDl0jJqc3eaxUOJUB9F+791buZnTj0/ESoGJPT3kyoW66SQfOvoGPomQEe/erftchq5NMuKNP5r"
        private val DIGEST1_BASE64 = ARGON1.substring(ARGON1.lastIndexOf('$') + 1)
        private val DIGEST1 = base64Codec.decode(DIGEST1_BASE64)

        private const val STR2 = " "
        private val STR2_BYTES = utf8TextCodec.decode(STR2)
        private const val STR2_SHA256 =
            "36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068"
        private val SALT2 = hexBinaryCodec.decode(STR2_SHA256.substring(0, 32))
        private const val ARGON2 = // Salt: Nqnn8clbgv+5l0PgxcTOlQ==
            $$"$argon2id$v=19$m=131072,t=4,p=1$Nqnn8clbgv+5l0PgxcTOlQ$x/RSe3X12/iJb19Bu3jO9FqQeMQodBavxjLn9j2V+ulQIcXY+nUeKda5TWjl5Tn2eH6CCy0JzrmTy000TyaSHtVBYGXxV27/"
        private val DIGEST2_BASE64 = ARGON2.substring(ARGON2.lastIndexOf('$') + 1)
        private val DIGEST2 = base64Codec.decode(DIGEST2_BASE64)

        private const val STR3 = "\n\n  "
        private val STR3_BYTES = utf8TextCodec.decode(STR3)
        private const val STR3_SHA256 =
            "4308ef96ad0e86f35c795a177206056556333e814e65bfc9cd04bb164f8d61eb"
        private val SALT3 = hexBinaryCodec.decode(STR3_SHA256.substring(0, 32))
        private const val ARGON3 = //Salt: Qwjvlq0OhvNceVoXcgYFZQ==
            $$"$argon2id$v=19$m=131072,t=4,p=1$Qwjvlq0OhvNceVoXcgYFZQ$+M4dfUMD1zmOBga1IcV/NaDmdsxr22+K3HSGwGW+0WEqfzUoMeABZCf/xkpBwNxf0BAGwDCj+MRzGut1MdJUIQGTXVVj/6z8"
        private val DIGEST3_BASE64 = ARGON3.substring(ARGON3.lastIndexOf('$') + 1)
        private val DIGEST3 = base64Codec.decode(DIGEST3_BASE64)

        private const val STR4 = "hello"
        private val STR4_BYTES = utf8TextCodec.decode(STR4)
        private const val STR4_SHA256 =
            "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
        private val SALT4 = hexBinaryCodec.decode(STR4_SHA256.substring(0, 32))
        private const val ARGON4 = // Salt: LPJNul+wow4m6Dsqxbning==
            $$"$argon2id$v=19$m=131072,t=4,p=1$LPJNul+wow4m6Dsqxbning$w86NXvmXuVB4Y2qL0oUp6smnFj74fWLExLaw5bbXRPxo9i4ebgPo/Fn2W+MdHYWB5KOi5N6aLAPbX6IhfO5bu06F3HBUK1fY"
        private val DIGEST4_BASE64 = ARGON4.substring(ARGON4.lastIndexOf('$') + 1)
        private val DIGEST4 = base64Codec.decode(DIGEST4_BASE64)

        private const val STR5 = "Hello World"
        private val STR5_BYTES = utf8TextCodec.decode(STR5)
        private const val STR5_SHA256 =
            "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e"
        private val SALT5 = hexBinaryCodec.decode(STR5_SHA256.substring(0, 32))
        private const val ARGON5 = // Salt: pZGm1Av0IEBKARczz7exkA==
            $$"$argon2id$v=19$m=131072,t=4,p=1$pZGm1Av0IEBKARczz7exkA$0TZbIZYWkvG47xenS/+wrFmiYC6S5woPAvXorv4+vxgTEX5hakW0MI2v4IuXaNIOdadpXXYr8YWFiZVeDYxDEN1CjLB9WKgI"
        private val DIGEST5_BASE64 = ARGON5.substring(ARGON5.lastIndexOf('$') + 1)
        private val DIGEST5 = base64Codec.decode(DIGEST5_BASE64)

        private const val STR6 = "This is a simple test! We are using Argon2_id.\nHave a good time."
        private val STR6_BYTES = utf8TextCodec.decode(STR6)
        private const val STR6_SHA256 =
            "6163f9727d2b2f770f94047405ceff3cda92383a94daef53b89641eb817dfd40"
        private val SALT6 = hexBinaryCodec.decode(STR6_SHA256.substring(0, 32))
        private const val ARGON6 = // Salt: YWP5cn0rL3cPlAR0Bc7/PA==
            $$"$argon2id$v=19$m=131072,t=4,p=1$YWP5cn0rL3cPlAR0Bc7/PA$pr0WZICBLKueiCWuOURz1vTdyCeovdFw8+1Rs6Hl+dJ9hQiurdtJ9liV8OyWXYgkRSo6WIOeccs0ySvP7vt1P4vJsMvMrB1d"
        private val DIGEST6_BASE64 = ARGON6.substring(ARGON6.lastIndexOf('$') + 1)
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

        val salt1 = ARGON1.substring(ARGON1.indexOf('$', 15) + 1, ARGON1.lastIndexOf('$'))
        val salt2 = ARGON2.substring(ARGON2.indexOf('$', 15) + 1, ARGON2.lastIndexOf('$'))
        val salt3 = ARGON3.substring(ARGON3.indexOf('$', 15) + 1, ARGON3.lastIndexOf('$'))
        val salt4 = ARGON4.substring(ARGON4.indexOf('$', 15) + 1, ARGON4.lastIndexOf('$'))
        val salt5 = ARGON5.substring(ARGON5.indexOf('$', 15) + 1, ARGON5.lastIndexOf('$'))
        val salt6 = ARGON6.substring(ARGON6.indexOf('$', 15) + 1, ARGON6.lastIndexOf('$'))

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
        assertContentEquals(DIGEST1, Argon2idHashing.digest(STR1_BYTES, SALT1))
        assertContentEquals(DIGEST2, Argon2idHashing.digest(STR2_BYTES, SALT2))
        assertContentEquals(DIGEST3, Argon2idHashing.digest(STR3_BYTES, SALT3))
        assertContentEquals(DIGEST4, Argon2idHashing.digest(STR4_BYTES, SALT4))
        assertContentEquals(DIGEST5, Argon2idHashing.digest(STR5_BYTES, SALT5))
        assertContentEquals(DIGEST6, Argon2idHashing.digest(STR6_BYTES, SALT6))
    }

    @Test
    fun testArgonBase64Length() {
        assertEquals(151, ARGON1.length)
        assertEquals(151, ARGON2.length)
        assertEquals(151, ARGON3.length)
        assertEquals(151, ARGON4.length)
        assertEquals(151, ARGON5.length)
        assertEquals(151, ARGON6.length)
    }

    @Test
    fun testDigest() = runTest {
        // Bytes
        assertContentEquals(DIGEST1, Argon2idHashing.digest(STR1_BYTES))
        assertContentEquals(DIGEST2, Argon2idHashing.digest(STR2_BYTES))
        assertContentEquals(DIGEST3, Argon2idHashing.digest(STR3_BYTES))
        assertContentEquals(DIGEST4, Argon2idHashing.digest(STR4_BYTES))
        assertContentEquals(DIGEST5, Argon2idHashing.digest(STR5_BYTES))
        assertContentEquals(DIGEST6, Argon2idHashing.digest(STR6_BYTES))

        // Base64
        assertEquals(DIGEST1_BASE64, Argon2idHashing.digest(STR1_BYTES, base64Codec))
        assertEquals(DIGEST2_BASE64, Argon2idHashing.digest(STR2_BYTES, base64Codec))
        assertEquals(DIGEST3_BASE64, Argon2idHashing.digest(STR3_BYTES, base64Codec))
        assertEquals(DIGEST4_BASE64, Argon2idHashing.digest(STR4_BYTES, base64Codec))
        assertEquals(DIGEST5_BASE64, Argon2idHashing.digest(STR5_BYTES, base64Codec))
        assertEquals(DIGEST6_BASE64, Argon2idHashing.digest(STR6_BYTES, base64Codec))
    }


    @Test
    fun testDigestLength() = runTest {
        // 72 bytes
        assertEquals(72, Argon2idHashing.digest(STR1_BYTES).size)
        assertEquals(72, Argon2idHashing.digest(STR2_BYTES).size)
        assertEquals(72, Argon2idHashing.digest(STR3_BYTES).size)
        assertEquals(72, Argon2idHashing.digest(STR4_BYTES).size)
        assertEquals(72, Argon2idHashing.digest(STR5_BYTES).size)
        assertEquals(72, Argon2idHashing.digest(STR6_BYTES).size)

        // 144 Hex-Chars
        assertEquals(144, Argon2idHashing.digest(STR1_BYTES, hexBinaryCodec).length)
        assertEquals(144, Argon2idHashing.digest(STR2_BYTES, hexBinaryCodec).length)
        assertEquals(144, Argon2idHashing.digest(STR3_BYTES, hexBinaryCodec).length)
        assertEquals(144, Argon2idHashing.digest(STR4_BYTES, hexBinaryCodec).length)
        assertEquals(144, Argon2idHashing.digest(STR5_BYTES, hexBinaryCodec).length)
        assertEquals(144, Argon2idHashing.digest(STR6_BYTES, hexBinaryCodec).length)

        // 96 Base64-Chars
        assertEquals(96, Argon2idHashing.digest(STR1_BYTES, base64Codec).length)
        assertEquals(96, Argon2idHashing.digest(STR2_BYTES, base64Codec).length)
        assertEquals(96, Argon2idHashing.digest(STR3_BYTES, base64Codec).length)
        assertEquals(96, Argon2idHashing.digest(STR4_BYTES, base64Codec).length)
        assertEquals(96, Argon2idHashing.digest(STR5_BYTES, base64Codec).length)
        assertEquals(96, Argon2idHashing.digest(STR6_BYTES, base64Codec).length)

        // 90 Z85-Chars
        assertEquals(90, Argon2idHashing.digest(STR1_BYTES, z85BinaryCodec).length)
        assertEquals(90, Argon2idHashing.digest(STR2_BYTES, z85BinaryCodec).length)
        assertEquals(90, Argon2idHashing.digest(STR3_BYTES, z85BinaryCodec).length)
        assertEquals(90, Argon2idHashing.digest(STR4_BYTES, z85BinaryCodec).length)
        assertEquals(90, Argon2idHashing.digest(STR5_BYTES, z85BinaryCodec).length)
        assertEquals(90, Argon2idHashing.digest(STR6_BYTES, z85BinaryCodec).length)
    }
}