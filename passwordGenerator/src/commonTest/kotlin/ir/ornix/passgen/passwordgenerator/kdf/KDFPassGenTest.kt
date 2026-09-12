package ir.ornix.passgen.passwordgenerator.kdf

import ir.ornix.passgen.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.codec.Base64BinaryCodec
import ir.ornix.passgen.codec.HexBinaryCodec
import ir.ornix.passgen.codec.Utf8TextCodec
import ir.ornix.passgen.codec.Z85BinaryCodec
import ir.ornix.passgen.codec.core.Encoder
import ir.ornix.passgen.passwordgenerator.model.InputHasher
import ir.ornix.passgen.passwordgenerator.model.PassEncoder
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.minutes

class KDFPassGenTest {

    private val utf8Codec = Utf8TextCodec()
    private val base64Codec = Base64BinaryCodec()
    private val bCryptCodec = BCryptBase64BinaryCodec()
    private val hexCodec = HexBinaryCodec(false)


    private val testCase1 = TestCase(
        input = "",
        argon2id = base64Codec.decode(
            "7nCeECf0DBRzNoB9W17t0fDl0jJqc3eaxUOJUB9F+791buZnTj0/ESoGJPT3kyoW66SQfOvoGPomQEe/erftchq5NMuKNP5r"
        ),
        bcrypt = bCryptCodec.decode("qnIYintMZA9VQ3qWzqxEtvw4tXyO5Py"),
        sha512 = hexCodec.decode(
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"
        ),
        sha256 = hexCodec.decode("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
    )

    private val testCase2 = TestCase(
        input = " ",
        argon2id = base64Codec.decode(
            "x/RSe3X12/iJb19Bu3jO9FqQeMQodBavxjLn9j2V+ulQIcXY+nUeKda5TWjl5Tn2eH6CCy0JzrmTy000TyaSHtVBYGXxV27/"
        ),
        bcrypt = bCryptCodec.decode("Qj5eZokAr/VoyT/B81gVcigGkXjv9e."),
        sha512 = hexCodec.decode(
            "f90ddd77e400dfe6a3fcf479b00b1ee29e7015c5bb8cd70f5f15b4886cc339275ff553fc8a053f8ddc7324f45168cffaf81f8c3ac93996f6536eef38e5e40768"
        ),
        sha256 = hexCodec.decode("36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068")
    )

    private val testCase3 = TestCase(
        input = "\n\n  ",
        argon2id = base64Codec.decode(
            "+M4dfUMD1zmOBga1IcV/NaDmdsxr22+K3HSGwGW+0WEqfzUoMeABZCf/xkpBwNxf0BAGwDCj+MRzGut1MdJUIQGTXVVj/6z8"
        ),
        bcrypt = bCryptCodec.decode("EmLve0j.AXy74JiSBFzVjpov6SCkJlK"),
        sha512 = hexCodec.decode(
            "6d317431699988eec1fbb52dadfe5d0bfb14d38398470401e1648c412244ee3c90bdbf627b21c37faf1e7ef00e5de1b69555362028a37b471da328c3fac59941"
        ),
        sha256 = hexCodec.decode("4308ef96ad0e86f35c795a177206056556333e814e65bfc9cd04bb164f8d61eb")
    )

    private val testCase4 = TestCase(
        input = "hello",
        argon2id = base64Codec.decode(
            "w86NXvmXuVB4Y2qL0oUp6smnFj74fWLExLaw5bbXRPxo9i4ebgPo/Fn2W+MdHYWB5KOi5N6aLAPbX6IhfO5bu06F3HBUK1fY"
        ),
        bcrypt = bCryptCodec.decode("aWoo2BxGM0sAUbeJTsGdDCs24koovhq"),
        sha512 = hexCodec.decode(
            "9b71d224bd62f3785d96d46ad3ea3d73319bfbc2890caadae2dff72519673ca72323c3d99ba5c11d7c7acc6e14b8c5da0c4663475c2e5c3adef46f73bcdec043"
        ),
        sha256 = hexCodec.decode("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824")
    )

    private val testCase5 = TestCase(
        input = "Hello World",
        argon2id = base64Codec.decode(
            "0TZbIZYWkvG47xenS/+wrFmiYC6S5woPAvXorv4+vxgTEX5hakW0MI2v4IuXaNIOdadpXXYr8YWFiZVeDYxDEN1CjLB9WKgI"
        ),
        bcrypt = bCryptCodec.decode("OqWhN04gD3je48K05tPyPNL4w8snY0O"),
        sha512 = hexCodec.decode(
            "2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b"
        ),
        sha256 = hexCodec.decode("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e")
    )

    private val testCases = listOf(testCase1, testCase2, testCase3, testCase4, testCase5)


    @Test
    fun testIllegalPasswordLength() = runTest {
        InputHasher.items.forEach { inputHasher ->
            PassEncoder.items.forEach { passEncoder ->
                val maxLength =
                    (inputHasher.hasher.outputByteSize / passEncoder.binaryBlockSize) * passEncoder.encodedBlockSize

                assertFailsWith<IllegalArgumentException> {
                    KDFPassGen(
                        inputDecoder = utf8Codec,
                        inputHasher = inputHasher,
                        passEncoder = passEncoder,
                        passwordLength = maxLength + 1
                    )
                }
            }
        }
    }


    @Test
    fun testFullLength() = runTest {
        InputHasher.items.forEach { inputHasher ->
            PassEncoder.items.forEach { passEncoder ->
                val maxLength =
                    (inputHasher.hasher.outputByteSize / passEncoder.binaryBlockSize) * passEncoder.encodedBlockSize

                testCases.forEach { testCase ->

                    val expected = testCase.getDigest(
                        inputHasher = inputHasher,
                        length = null,
                        encoder = passEncoder.encoder
                    )

                    assertEquals(
                        expected,
                        KDFPassGen(
                            inputDecoder = utf8Codec,
                            inputHasher = inputHasher,
                            passEncoder = passEncoder,
                            passwordLength = maxLength
                        ).generate(testCase.input)
                    )
                }
            }
        }
    }


    @Test
    fun testCustomLength() = runTest(timeout = 2.minutes) {
        InputHasher.items.forEach { inputHasher ->
            PassEncoder.items.forEach { passEncoder ->
                testCases.forEach { testCase ->
                    val maxLength =
                        (inputHasher.hasher.outputByteSize / passEncoder.binaryBlockSize) * passEncoder.encodedBlockSize

                    ((maxLength - 5)..maxLength).forEach { length ->
                        val expected = testCase.getDigest(
                            inputHasher = inputHasher,
                            length = length,
                            encoder = passEncoder.encoder
                        )

                        assertEquals(
                            expected,
                            KDFPassGen(
                                inputDecoder = utf8Codec,
                                inputHasher = inputHasher,
                                passEncoder = passEncoder,
                                passwordLength = length
                            ).generate(testCase.input)
                        )
                    }
                }
            }
        }
    }


    private class TestCase(
        val input: String,
        val argon2id: ByteArray,
        val bcrypt: ByteArray,
        val sha512: ByteArray,
        val sha256: ByteArray
    ) {

        /**
         * @param length Set null for full-length
         */
        fun getDigest(inputHasher: InputHasher, length: Int?, encoder: Encoder): String {
            val binaryBlockSize: Int
            val encodedBlockSize: Int

            val bytes = when (inputHasher) {
                is InputHasher.ARGON2ID -> argon2id
                is InputHasher.BCrypt -> bcrypt
                is InputHasher.SHA512 -> sha512
                is InputHasher.SHA256 -> sha256
            }

            when (encoder) {
                is HexBinaryCodec -> {
                    binaryBlockSize = 1
                    encodedBlockSize = 2
                }

                is Base64BinaryCodec -> {
                    binaryBlockSize = 3
                    encodedBlockSize = 4
                }

                is Z85BinaryCodec -> {
                    binaryBlockSize = 4
                    encodedBlockSize = 5
                }

                else -> {
                    throw UnsupportedOperationException("This Codec is not supported!")
                }
            }

            val maxSize = bytes.size - (bytes.size % binaryBlockSize)
            val validBytes = bytes.copyOfRange(0, maxSize)
            val encoded = encoder.encode(validBytes)

            check(encoded.length == (maxSize / binaryBlockSize) * encodedBlockSize) {
                "Encoded size must be a multiple of encodedBlockSize!"
            }

            return if (length == null) encoded
            else encoded.substring(0, length)
        }
    }

}