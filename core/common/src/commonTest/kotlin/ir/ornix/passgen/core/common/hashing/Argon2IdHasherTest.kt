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


        private val testCase12 =
            TestCase( // input: random 64 bytes    Salt: N/mcagyvJ/vNhTEB9djUKw==
                inputBytes = hexBinaryCodec.decode("c7d15d8dbd9248c783662d174711e7c581333e3e2806897ed80d23e3cb1e714c54dca45a7682fe37d2fdc260deb9ec7c6ffc20ca78864c6744e716005600447a"),
                inputSha256 = "37f99c6a0caf27fbcd853101f5d8d42b1578318ba51b4992de53b6368d3f421e",
                phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$N/mcagyvJ/vNhTEB9djUKw$iqcZbIvWdBlgfcsMqqMPokrig0oCuMz7XR6eYzCi2S2F4nos50HADyMkq5d5TcLC0+XM997OiU0EWH6CbzqJeg"
            )

        private val testCase13 =
            TestCase( // input: random 64 bytes    Salt: vRFKCP0/NrQ9zXpvE8TgBQ==
                inputBytes = hexBinaryCodec.decode("fcb75e8d004061c9fd9c948bd800d3caedea6b721bf475aed74a926b0fe41bf6bbafc9ab6109bdeb7ced9a5f21e1cc17d61c4fa7eb91ed18584f3511c273acc8"),
                inputSha256 = "bd114a08fd3f36b43dcd7a6f13c4e005f7ecaf4cd7a8a21f598aa7017b8629ee",
                phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$vRFKCP0/NrQ9zXpvE8TgBQ$QeZ32H9jV613SUXHBJcS07Er2ejcCAHCqdT3R572ixu4XROKbrrnw27Xlg+Q92LDWeclLRJB5RvMoE+nrF1CoQ"
            )


        private val testCase14 =
            TestCase( // input: random 64 bytes    Salt: SbohDhERjApxKrjhfcGa9Q==
                inputBytes = hexBinaryCodec.decode("1a8eb975709c30d365b67244966072eac6f8a369ef7029fcb9677c43e7c327d356e64ae15bc5c03ba96f5ff9b56d4bf92f73a10e87f510119f5949ec0c9feb89"),
                inputSha256 = "49ba210e11118c0a712ab8e17dc19af5dc73c4b21d28be253a206410604a3373",
                phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$SbohDhERjApxKrjhfcGa9Q$9QEiT577XaMzqlwPHfrIfcukmzj/7qv1h7iaORRpncoylLziXax0j5B0gjOiTYH+TGUO8qkv1cDSznhm6cGBZQ"
            )


        private val testCase15 =
            TestCase( // input: random 72 bytes    Salt: I3O8GYP8v+17yTinMTKLLw==
                inputBytes = hexBinaryCodec.decode("8a23e897481aff2e8e07c98d6379bebb35c951f0b4d33d802eb66c39a9b39eab337e12674c4093ab48860d169916e1aaeea13006bcaacb45dc9e538e93965f2a7135fcf467fb94b8"),
                inputSha256 = "2373bc1983fcbfed7bc938a731328b2f5c0f7df83b58e9bcbd3fefc68e83e5b1",
                phcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$I3O8GYP8v+17yTinMTKLLw$b8Z5kxebd6zoqTEPPBX7sP0gPGbuNTmOc+y2JFrhucj+snBXNvsWCTkrSqqOHsH8xVw5tUEYjI7L/FMMP6RKRQ"
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
            testCase11,
            testCase12,
            testCase13,
            testCase14,
            testCase15
        )
    }


    @Test
    fun testSalt() = runTest {

        // Salt size should be 16 bytes
        testCases.forEachIndexed { index, testCase ->
            assertEquals(
                16,
                testCase.salt.size,
                "Test case $index failed. Salt size should be 16 bytes."
            )
        }

        testCases.forEachIndexed { index, testCase ->
            val salt = testCase.phcEncoded.substring(
                testCase.phcEncoded.indexOf('$', 15) + 1,
                testCase.phcEncoded.lastIndexOf('$')
            )

            // 22 Base64-Chars
            assertEquals(
                22,
                salt.length,
                "Test case $index failed. Salt should be 22 Base64-Chars."
            )

            assertContentEquals(
                testCase.salt,
                base64Codec.decode(salt),
                "Test case $index failed. Salt should be the same."
            )
        }
    }

    @Test
    fun testCustomSalt() = runTest {
        // Getting digest with custom salt, should return the same digest
        testCases.forEachIndexed { index, testCase ->
            assertContentEquals(
                testCase.digest,
                Argon2IdHasher.digest(testCase.inputBytes, testCase.salt),
                "Test case $index failed. Custom salt should return the same digest."
            )
        }
    }

    @Test
    fun testArgonBase64Length() {
        testCases.forEachIndexed { index, testCase ->
            assertEquals(
                141,
                testCase.phcEncoded.length,
                "Test case $index failed. Argon2id should be 141 chars."
            )
        }
    }

    @Test
    fun testDigest() = runTest {
        // Bytes
        testCases.forEachIndexed { index, testCase ->
            assertContentEquals(
                testCase.digest,
                Argon2IdHasher.digest(testCase.inputBytes),
                "Test case $index failed. Digest bytes should be the same."
            )
        }

        // Base64
        testCases.forEachIndexed { index, testCase ->
            assertEquals(
                "${testCase.digestBase64}==",
                Argon2IdHasher.digest(testCase.inputBytes, base64Codec),
                "Test case $index failed. Digest should be the same."
            )
        }
    }


    @Test
    fun testDigestLength() = runTest {
        // 64 bytes
        testCases.forEachIndexed { index, testCase ->
            assertEquals(
                64,
                Argon2IdHasher.digest(testCase.inputBytes).size,
                "Test case $index failed. Digest should be 64 bytes."
            )
        }

        // 128 Hex-Chars
        testCases.forEachIndexed { index, testCase ->
            assertEquals(
                128,
                Argon2IdHasher.digest(testCase.inputBytes, hexBinaryCodec).length,
                "Test case $index failed. Digest should be 128 Hex-Chars."
            )
        }

        // 88 Base64-Chars
        testCases.forEachIndexed { index, testCase ->
            assertEquals(
                88,
                Argon2IdHasher.digest(testCase.inputBytes, base64Codec).length,
                "Test case $index failed. Digest should be 88 Base64-Chars."
            )
        }

        // 80 Z85-Chars
        testCases.forEachIndexed { index, testCase ->
            assertEquals(
                80,
                Argon2IdHasher.digest(testCase.inputBytes, z85BinaryCodec).length,
                "Test case $index failed. Digest should be 80 Z85-Chars."
            )
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