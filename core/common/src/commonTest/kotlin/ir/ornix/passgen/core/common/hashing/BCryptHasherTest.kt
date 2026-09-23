package ir.ornix.passgen.core.common.hashing

import ir.ornix.passgen.core.common.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Utf8TextCodec
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


        private val testCase1 = TestCase(
            inputBytes = utf8TextCodec.decode(""),
            inputSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            bcrypt = $$"$2a$12$25BCOnh6F/QY89RGkU83H.qnIYintMZA9VQ3qWzqxEtvw4tXyO5Py"
        )

        private val testCase2 = TestCase(
            inputBytes = hexBinaryCodec.decode("00"),
            inputSha256 = "6e340b9cffb37a989ca544e6bb780a2c78901d3fb33738768511a30617afa01d",
            bcrypt = $$"$2b$12$ZhOJlN8xcnganSRks1eIJ.QyBT0dMyd4sYIeavoaacRnEUtpGy.M."
        )

        private val testCase3 = TestCase(
            inputBytes = hexBinaryCodec.decode("00000000"),
            inputSha256 = "df3f619804a92fdb4057192dc43dd748ea778adc52bc498ce80524c014b81119",
            bcrypt = $$"$2b$12$1x7fk.QnJ7r.TvirvB1VQ.jWwDU/Nlqb0p4f1cGdsNWP3aYqy9oui"
        )

        private val testCase4 = TestCase(
            inputBytes = hexBinaryCodec.decode("00123456"),
            inputSha256 = "a4f01a4e3fc21d8d47b4ebc53dbccbfaa8ac316da628768ef5296f8d8d58f378",
            bcrypt = $$"$2b$12$nN.YRh9AFWzFrMtDNZxJ8e6WMqr0PHCC54DRx/8StFwpFA6NjYRUC"
        )


        private val testCase5 = TestCase(
            inputBytes = hexBinaryCodec.decode("12340056"),
            inputSha256 = "b77a38f1a4104338bb15867993c507bd2a4fd53565a06376c8960b0fdc06ea74",
            bcrypt = $$"$2b$12$r1m26YOOOxg5DWX3i6SFtOzuVI8ysXMEzE38PYaQ133KPyYz2JhHi"
        )


        private val testCase6 = TestCase(
            inputBytes = hexBinaryCodec.decode("1234567800"),
            inputSha256 = "367119ea64bce8247631617b0f9fa9a3933aed64f0ef38e900c55d8c925c1e1b",
            bcrypt = $$"$2b$12$LlCX4kQ64AP0KUD5B38nmuMVh3/69cyxJAQRIR2BIoIN9fwTT2Sla"
        )


        private val testCase7 = TestCase(
            inputBytes = utf8TextCodec.decode(" "),
            inputSha256 = "36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068",
            bcrypt = $$"$2b$12$Loll6ajZet83jyNevaRMjOQj5eZokAr/VoyT/B81gVcigGkXjv9e."
        )


        private val testCase8 = TestCase(
            inputBytes = utf8TextCodec.decode("\n\n  "),
            inputSha256 = "4308ef96ad0e86f35c795a177206056556333e814e65bfc9cd04bb164f8d61eb",
            bcrypt = $$"$2a$12$OuhtjoyMftLacTmVaeWDXOEmLve0j.AXy74JiSBFzVjpov6SCkJlK"
        )


        private val testCase9 = TestCase(
            inputBytes = utf8TextCodec.decode("hello"),
            inputSha256 = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
            bcrypt = $$"$2a$12$JNHLsj8umu2k4BqovZlgleaWoo2BxGM0sAUbeJTsGdDCs24koovhq"
        )


        private val testCase10 = TestCase(
            inputBytes = utf8TextCodec.decode("Hello World"),
            inputSha256 = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e",
            bcrypt = $$"$2a$12$nXEkz.tyGC/I.Paxx5cvi.OqWhN04gD3je48K05tPyPNL4w8snY0O"
        )


        private val testCase11 = TestCase(
            inputBytes = utf8TextCodec.decode("This is a simple test! We are using BCrypt.\nHave a good time."),
            inputSha256 = "908a884659b292ce28c17f63fde86a1c9a9721e0f042a56cb22b6143bb7df96b",
            bcrypt = $$"$2b$12$iGoGPjkwiq2muV7h9cfoF.Pdh3Du1kcUNRkGtlOJZByLVCNTOn5cW"
        )


        private val testCases = listOf(
            testCase1,
            testCase2,
            testCase3,
            testCase4,
            testCase5,
            testCase6,
            testCase7,
            testCase8,
            testCase9,
            testCase10,
            testCase11
        )
    }


    @Test
    fun testSalt() = runTest {

        // Salt size should be 16 bytes
        testCases.forEach { testCase ->
            assertEquals(16, testCase.salt.size)
        }

        testCases.forEach { testCase ->
            val salt = testCase.bcrypt.substring(testCase.bcrypt.lastIndexOf('$') + 1, 29)

            // 22 BCrypt-Base64-Chars
            assertEquals(22, salt.length)

            assertContentEquals(testCase.salt, bCryptBase64Codec.decode(salt))
        }
    }

    @Test
    fun testCustomSalt() = runTest {
        // Getting digest with custom salt, should return the same digest
        testCases.forEach { testCase ->
            assertContentEquals(
                testCase.digest,
                bCryptHashing.digest(testCase.inputBytes, testCase.salt)
            )
        }
    }

    @Test
    fun testLength() {
        testCases.forEach { testCase ->
            assertEquals(60, testCase.bcrypt.length)
        }
    }


    @Test
    fun testDigest() = runTest {
        // Bytes
        testCases.forEach { testCase ->
            assertContentEquals(testCase.digest, bCryptHashing.digest(testCase.inputBytes))
        }

        // BCrypt-Base64
        testCases.forEach { testCase ->
            assertEquals(
                testCase.digestBcryptBase64,
                bCryptHashing.digest(testCase.inputBytes, bCryptBase64Codec)
            )
        }
    }


    @Test
    fun testDigestLength() = runTest {
        // 23 bytes
        testCases.forEach { testCase ->
            assertEquals(23, bCryptHashing.digest(testCase.inputBytes).size)
        }

        // 46 Hex-Chars
        testCases.forEach { testCase ->
            assertEquals(46, bCryptHashing.digest(testCase.inputBytes, hexBinaryCodec).length)
        }

        // 31 BCrypt-Base64-Chars
        testCases.forEach { testCase ->
            assertEquals(31, bCryptHashing.digest(testCase.inputBytes, bCryptBase64Codec).length)
        }
    }


    private class TestCase(
        val inputBytes: ByteArray,
        val inputSha256: String,
        val bcrypt: String,
    ) {
        val salt = hexBinaryCodec.decode(inputSha256.substring(0, 32))
        val digestBcryptBase64 = bcrypt.substring(29)
        val digest = bCryptBase64Codec.decode(digestBcryptBase64)
    }
}