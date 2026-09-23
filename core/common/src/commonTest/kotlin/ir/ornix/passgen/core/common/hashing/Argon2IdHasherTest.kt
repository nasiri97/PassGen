package ir.ornix.passgen.core.common.hashing

import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Utf8TextCodec
import ir.ornix.passgen.core.common.codec.Z85BinaryCodec
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


        private val testCase1 = TestCase( // Salt: 47DEQpj8HBSa+/TImW+5JA==
            inputBytes = utf8TextCodec.decode(""),
            inputSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$47DEQpj8HBSa+/TImW+5JA$SIccW0Qtg/lQJyg+5S5HaSe5hzUqpzlpT0FZHH24rpPHsvua5MjBldLxXVCGYztmsJtLiU7D7QZWzN/mlNKkvQ"
        )


        private val testCase2 = TestCase( // Salt: bjQLnP+zepicpUTmu3gKLA==
            inputBytes = hexBinaryCodec.decode("00"),
            inputSha256 = "6e340b9cffb37a989ca544e6bb780a2c78901d3fb33738768511a30617afa01d",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$bjQLnP+zepicpUTmu3gKLA$pvEbvnWSbP6Bi6vBzsRRksk77aARRKWJ7J/HnU4HC8HT6YcjLWcH4DNMnyV3KjFMT5s5dJw8KyYwsXx/28UTlQ"
        )

        private val testCase3 = TestCase( // Salt: 3z9hmASpL9tAVxktxD3XSA==
            inputBytes = hexBinaryCodec.decode("00000000"),
            inputSha256 = "df3f619804a92fdb4057192dc43dd748ea778adc52bc498ce80524c014b81119",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$3z9hmASpL9tAVxktxD3XSA$5zRXagG29lNXHYl8Oeqgw3iNlK0ZF2lsKTuppPuMT7RfbD2125UZSd8dpT1L8Yc90VOQIpFPapZMwgH8AEgzsA"
        )

        private val testCase4 = TestCase( // Salt: pPAaTj/CHY1HtOvFPbzL+g==
            inputBytes = hexBinaryCodec.decode("00123456"),
            inputSha256 = "a4f01a4e3fc21d8d47b4ebc53dbccbfaa8ac316da628768ef5296f8d8d58f378",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$pPAaTj/CHY1HtOvFPbzL+g$JK8RtYkyGjWCXEJDQxqlbzYh1uMqKlSeJC+7RGJNMQQMbjXMV/Sv7IkuGLU73G3At320+7wSafLiGMuWxxU+Ww"
        )

        private val testCase5 = TestCase( // Salt: t3o48aQQQzi7FYZ5k8UHvQ==
            inputBytes = hexBinaryCodec.decode("12340056"),
            inputSha256 = "b77a38f1a4104338bb15867993c507bd2a4fd53565a06376c8960b0fdc06ea74",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$t3o48aQQQzi7FYZ5k8UHvQ$AfLH52KMe/3F87RGf+J3bpc+05u54MIhXZG3VRqrHZ3BpN/WJztqxiYlOENHztX00lXAyivQIEROoXBO4ROovw"
        )

        private val testCase6 = TestCase( // Salt: NnEZ6mS86CR2MWF7D5+pow==
            inputBytes = hexBinaryCodec.decode("1234567800"),
            inputSha256 = "367119ea64bce8247631617b0f9fa9a3933aed64f0ef38e900c55d8c925c1e1b",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$NnEZ6mS86CR2MWF7D5+pow$1woN2P6DWTlVhpHa5KPVCI64+h0FT9GasloAvctP4iZbPwnZ+sOOdxUGqNlH0Ca3AaLfpelJZ1atGInbAtrdLQ"
        )


        private val testCase7 = TestCase( // Salt: Nqnn8clbgv+5l0PgxcTOlQ==
            inputBytes = utf8TextCodec.decode(" "),
            inputSha256 = "36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$Nqnn8clbgv+5l0PgxcTOlQ$1vjyDAtqCH+KQPxO0mhBR4zbIoF2919kBVnLuECm8M8Kq+3vc9oyLS6InLO2whmP0CCgTcNd83yKBqZTfNpzYw"
        )


        private val testCase8 = TestCase( //Salt: Qwjvlq0OhvNceVoXcgYFZQ==
            inputBytes = utf8TextCodec.decode("\n\n  "),
            inputSha256 = "4308ef96ad0e86f35c795a177206056556333e814e65bfc9cd04bb164f8d61eb",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$Qwjvlq0OhvNceVoXcgYFZQ$jNwuBTBCAMjy3anuNFnZZqwhBB6BNpF+1BFFMX8xpVIzXVdPSOEp0/3nR00KuzAJivSpCGORKRlotWhqvnLVbw"
        )


        private val testCase9 = TestCase( // Salt: LPJNul+wow4m6Dsqxbning==
            inputBytes = utf8TextCodec.decode("hello"),
            inputSha256 = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$LPJNul+wow4m6Dsqxbning$Jomso/aWbL3bXqb0Y+WuQOdMiveFYCBBC9FClw5pAihWocz0xFhc+Qns2PJZ3PhBp8doN1Eb9dyx36q1B50lBg"
        )


        private val testCase10 = TestCase( // Salt: pZGm1Av0IEBKARczz7exkA==
            inputBytes = utf8TextCodec.decode("Hello World"),
            inputSha256 = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$pZGm1Av0IEBKARczz7exkA$whbMNRoF5/S71AQakvFTqefPzAnppbAGVdGk3fKf9qselK/JGaY5fDawfqfVQ6hkTVWnMVoHzN7GiuwDz9a6/A"
        )


        private val testCase11 = TestCase( // Salt: YWP5cn0rL3cPlAR0Bc7/PA==
            inputBytes = utf8TextCodec.decode("This is a simple test! We are using Argon2_id.\nHave a good time."),
            inputSha256 = "6163f9727d2b2f770f94047405ceff3cda92383a94daef53b89641eb817dfd40",
            phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$YWP5cn0rL3cPlAR0Bc7/PA$MJc2LdHkKAyYx8JHXC676Hrqts8Df80wSI5GXmyLdo4b5WHyGxFKesjFFw2uOGRtpzs7kmBOoxhaJffe7wkwuQ"
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
            val salt = testCase.phcEncoded.substring(
                testCase.phcEncoded.indexOf('$', 15) + 1,
                testCase.phcEncoded.lastIndexOf('$')
            )

            // 22 Base64-Chars
            assertEquals(22, salt.length)

            assertContentEquals(testCase.salt, base64Codec.decode(salt))
        }
    }

    @Test
    fun testCustomSalt() = runTest {
        // Getting digest with custom salt, should return the same digest
        testCases.forEach { testCase ->
            assertContentEquals(
                testCase.digest,
                Argon2IdHasher.digest(testCase.inputBytes, testCase.salt)
            )
        }
    }

    @Test
    fun testArgonBase64Length() {
        testCases.forEach { testCase ->
            assertEquals(141, testCase.phcEncoded.length)
        }
    }

    @Test
    fun testDigest() = runTest {
        // Bytes
        testCases.forEach { testCase ->
            assertContentEquals(testCase.digest, Argon2IdHasher.digest(testCase.inputBytes))
        }

        // Base64
        testCases.forEach { testCase ->
            assertEquals(
                "${testCase.digestBase64}==",
                Argon2IdHasher.digest(testCase.inputBytes, base64Codec)
            )
        }
    }


    @Test
    fun testDigestLength() = runTest {
        // 72 bytes
        testCases.forEach { testCase ->
            assertEquals(64, Argon2IdHasher.digest(testCase.inputBytes).size)
        }

        // 144 Hex-Chars
        testCases.forEach { testCase ->
            assertEquals(128, Argon2IdHasher.digest(testCase.inputBytes, hexBinaryCodec).length)
        }

        // 88 Base64-Chars
        testCases.forEach { testCase ->
            assertEquals(88, Argon2IdHasher.digest(testCase.inputBytes, base64Codec).length)
        }

        // 80 Z85-Chars
        testCases.forEach { testCase ->
            assertEquals(80, Argon2IdHasher.digest(testCase.inputBytes, z85BinaryCodec).length)
        }
    }

    private class TestCase(
        val inputBytes: ByteArray,
        val inputSha256: String,
        val phcEncoded: String
    ) {
        val salt = hexBinaryCodec.decode(inputSha256.substring(0, 32))
        val digestBase64 = phcEncoded.substring(phcEncoded.lastIndexOf('$') + 1)
        val digest = base64Codec.decode(digestBase64)
    }
}