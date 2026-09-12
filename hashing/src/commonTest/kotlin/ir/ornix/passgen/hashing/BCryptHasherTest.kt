package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.codec.HexBinaryCodec
import ir.ornix.passgen.codec.Utf8TextCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class BCryptHasherTest {

    companion object {
        private val bCryptHashing = BCryptHasher()
        private val bCryptBase64Codec = BCryptBase64BinaryCodec()
        private val utf8TextCodec = Utf8TextCodec()
        private val hexBinaryCodec = HexBinaryCodec(false)

        private const val STR1 = ""
        private val STR1_BYTES = utf8TextCodec.decode(STR1)
        private const val STR1_SHA256 =
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        private val SALT1 = hexBinaryCodec.decode(STR1_SHA256.substring(0, 32))
        private const val BCRYPT1 = $$"$2a$12$25BCOnh6F/QY89RGkU83H.qnIYintMZA9VQ3qWzqxEtvw4tXyO5Py"
        private val DIGEST1_BCRYPT_BASE64 = BCRYPT1.substring(29)
        private val DIGEST1 = bCryptBase64Codec.decode(DIGEST1_BCRYPT_BASE64)

        private const val STR2 = " "
        private val STR2_BYTES = utf8TextCodec.decode(STR2)
        private const val STR2_SHA256 =
            "36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068"
        private val SALT2 = hexBinaryCodec.decode(STR2_SHA256.substring(0, 32))
        private const val BCRYPT2 = $$"$2b$12$Loll6ajZet83jyNevaRMjOQj5eZokAr/VoyT/B81gVcigGkXjv9e."
        private val DIGEST2_BCRYPT_BASE64 = BCRYPT2.substring(29)
        private val DIGEST2 = bCryptBase64Codec.decode(DIGEST2_BCRYPT_BASE64)

        private const val STR3 = "\n\n  "
        private val STR3_BYTES = utf8TextCodec.decode(STR3)
        private const val STR3_SHA256 =
            "4308ef96ad0e86f35c795a177206056556333e814e65bfc9cd04bb164f8d61eb"
        private val SALT3 = hexBinaryCodec.decode(STR3_SHA256.substring(0, 32))
        private const val BCRYPT3 = $$"$2a$12$OuhtjoyMftLacTmVaeWDXOEmLve0j.AXy74JiSBFzVjpov6SCkJlK"
        private val DIGEST3_BCRYPT_BASE64 = BCRYPT3.substring(29)
        private val DIGEST3 = bCryptBase64Codec.decode(DIGEST3_BCRYPT_BASE64)

        private const val STR4 = "hello"
        private val STR4_BYTES = utf8TextCodec.decode(STR4)
        private const val STR4_SHA256 =
            "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
        private val SALT4 = hexBinaryCodec.decode(STR4_SHA256.substring(0, 32))
        private const val BCRYPT4 = $$"$2a$12$JNHLsj8umu2k4BqovZlgleaWoo2BxGM0sAUbeJTsGdDCs24koovhq"
        private val DIGEST4_BCRYPT_BASE64 = BCRYPT4.substring(29)
        private val DIGEST4 = bCryptBase64Codec.decode(DIGEST4_BCRYPT_BASE64)

        private const val STR5 = "Hello World"
        private val STR5_BYTES = utf8TextCodec.decode(STR5)
        private const val STR5_SHA256 =
            "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e"
        private val SALT5 = hexBinaryCodec.decode(STR5_SHA256.substring(0, 32))
        private const val BCRYPT5 = $$"$2a$12$nXEkz.tyGC/I.Paxx5cvi.OqWhN04gD3je48K05tPyPNL4w8snY0O"
        private val DIGEST5_BCRYPT_BASE64 = BCRYPT5.substring(29)
        private val DIGEST5 = bCryptBase64Codec.decode(DIGEST5_BCRYPT_BASE64)

        private const val STR6 = "This is a simple test! We are using BCrypt.\nHave a good time."
        private val STR6_BYTES = utf8TextCodec.decode(STR6)
        private const val STR6_SHA256 =
            "908a884659b292ce28c17f63fde86a1c9a9721e0f042a56cb22b6143bb7df96b"
        private val SALT6 = hexBinaryCodec.decode(STR6_SHA256.substring(0, 32))
        private const val BCRYPT6 = $$"$2b$12$iGoGPjkwiq2muV7h9cfoF.Pdh3Du1kcUNRkGtlOJZByLVCNTOn5cW"
        private val DIGEST6_BCRYPT_BASE64 = BCRYPT6.substring(29)
        private val DIGEST6 = bCryptBase64Codec.decode(DIGEST6_BCRYPT_BASE64)
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

        val salt1 = BCRYPT1.substring(BCRYPT1.lastIndexOf('$') + 1, 29)
        val salt2 = BCRYPT2.substring(BCRYPT2.lastIndexOf('$') + 1, 29)
        val salt3 = BCRYPT3.substring(BCRYPT3.lastIndexOf('$') + 1, 29)
        val salt4 = BCRYPT4.substring(BCRYPT4.lastIndexOf('$') + 1, 29)
        val salt5 = BCRYPT5.substring(BCRYPT5.lastIndexOf('$') + 1, 29)
        val salt6 = BCRYPT6.substring(BCRYPT6.lastIndexOf('$') + 1, 29)

        // 22 BCrypt-Base64-Chars
        assertEquals(22, salt1.length)
        assertEquals(22, salt2.length)
        assertEquals(22, salt3.length)
        assertEquals(22, salt4.length)
        assertEquals(22, salt5.length)
        assertEquals(22, salt6.length)

        assertContentEquals(SALT1, bCryptBase64Codec.decode(salt1))
        assertContentEquals(SALT2, bCryptBase64Codec.decode(salt2))
        assertContentEquals(SALT3, bCryptBase64Codec.decode(salt3))
        assertContentEquals(SALT4, bCryptBase64Codec.decode(salt4))
        assertContentEquals(SALT5, bCryptBase64Codec.decode(salt5))
        assertContentEquals(SALT6, bCryptBase64Codec.decode(salt6))
    }

    @Test
    fun testCustomSalt() = runTest {
        // Getting digest with custom salt, should return the same digest
        assertContentEquals(DIGEST1, bCryptHashing.digest(STR1_BYTES, SALT1))
        assertContentEquals(DIGEST2, bCryptHashing.digest(STR2_BYTES, SALT2))
        assertContentEquals(DIGEST3, bCryptHashing.digest(STR3_BYTES, SALT3))
        assertContentEquals(DIGEST4, bCryptHashing.digest(STR4_BYTES, SALT4))
        assertContentEquals(DIGEST5, bCryptHashing.digest(STR5_BYTES, SALT5))
        assertContentEquals(DIGEST6, bCryptHashing.digest(STR6_BYTES, SALT6))
    }

    @Test
    fun testLength() {
        assertEquals(60, BCRYPT1.length)
        assertEquals(60, BCRYPT2.length)
        assertEquals(60, BCRYPT3.length)
        assertEquals(60, BCRYPT4.length)
        assertEquals(60, BCRYPT5.length)
        assertEquals(60, BCRYPT6.length)
    }

    @Test
    fun testDigest() = runTest {
        // Bytes
        assertContentEquals(DIGEST1, bCryptHashing.digest(STR1_BYTES))
        assertContentEquals(DIGEST2, bCryptHashing.digest(STR2_BYTES))
        assertContentEquals(DIGEST3, bCryptHashing.digest(STR3_BYTES))
        assertContentEquals(DIGEST4, bCryptHashing.digest(STR4_BYTES))
        assertContentEquals(DIGEST5, bCryptHashing.digest(STR5_BYTES))
        assertContentEquals(DIGEST6, bCryptHashing.digest(STR6_BYTES))

        // BCrypt-Base64
        assertEquals(DIGEST1_BCRYPT_BASE64, bCryptHashing.digest(STR1_BYTES, bCryptBase64Codec))
        assertEquals(DIGEST2_BCRYPT_BASE64, bCryptHashing.digest(STR2_BYTES, bCryptBase64Codec))
        assertEquals(DIGEST3_BCRYPT_BASE64, bCryptHashing.digest(STR3_BYTES, bCryptBase64Codec))
        assertEquals(DIGEST4_BCRYPT_BASE64, bCryptHashing.digest(STR4_BYTES, bCryptBase64Codec))
        assertEquals(DIGEST5_BCRYPT_BASE64, bCryptHashing.digest(STR5_BYTES, bCryptBase64Codec))
        assertEquals(DIGEST6_BCRYPT_BASE64, bCryptHashing.digest(STR6_BYTES, bCryptBase64Codec))
    }


    @Test
    fun testDigestLength() = runTest {
        // 23 bytes
        assertEquals(23, bCryptHashing.digest(STR1_BYTES).size)
        assertEquals(23, bCryptHashing.digest(STR2_BYTES).size)
        assertEquals(23, bCryptHashing.digest(STR3_BYTES).size)
        assertEquals(23, bCryptHashing.digest(STR4_BYTES).size)
        assertEquals(23, bCryptHashing.digest(STR5_BYTES).size)
        assertEquals(23, bCryptHashing.digest(STR6_BYTES).size)

        // 46 Hex-Chars
        assertEquals(46, bCryptHashing.digest(STR1_BYTES, hexBinaryCodec).length)
        assertEquals(46, bCryptHashing.digest(STR2_BYTES, hexBinaryCodec).length)
        assertEquals(46, bCryptHashing.digest(STR3_BYTES, hexBinaryCodec).length)
        assertEquals(46, bCryptHashing.digest(STR4_BYTES, hexBinaryCodec).length)
        assertEquals(46, bCryptHashing.digest(STR5_BYTES, hexBinaryCodec).length)
        assertEquals(46, bCryptHashing.digest(STR6_BYTES, hexBinaryCodec).length)

        // 31 BCrypt-Base64-Chars
        assertEquals(31, bCryptHashing.digest(STR1_BYTES, bCryptBase64Codec).length)
        assertEquals(31, bCryptHashing.digest(STR2_BYTES, bCryptBase64Codec).length)
        assertEquals(31, bCryptHashing.digest(STR3_BYTES, bCryptBase64Codec).length)
        assertEquals(31, bCryptHashing.digest(STR4_BYTES, bCryptBase64Codec).length)
        assertEquals(31, bCryptHashing.digest(STR5_BYTES, bCryptBase64Codec).length)
        assertEquals(31, bCryptHashing.digest(STR6_BYTES, bCryptBase64Codec).length)
    }

}