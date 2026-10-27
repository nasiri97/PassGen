package ir.ornix.passgen.core.common.passwordgenerator.kdf

import ir.ornix.passgen.core.common.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder


private val base64Codec = Base64BinaryCodec()
private val bCryptCodec = BCryptBase64BinaryCodec()
private val hexCodec = HexBinaryCodec(false)


private suspend fun createKnownVector(
    input: String,
    expectedSha256: String,
    expectedSha512: String,
    expectedBcryptMcfEncoded: String,
    expectedArgon2idPhcEncoded: String
) = KnownVector(
    input = input,
    expectedSha256 = hexCodec.decode(expectedSha256),
    expectedSha512 = hexCodec.decode(expectedSha512),
    expectedBcrypt = bCryptCodec.decode(
        expectedBcryptMcfEncoded.substring(29)
    ),
    expectedArgon2id = base64Codec.decode(
        expectedArgon2idPhcEncoded.substring(
            expectedArgon2idPhcEncoded.lastIndexOf('$') + 1
        )
    )
)

private suspend fun knownVector1() = createKnownVector(
    input = "",
    expectedSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    expectedSha512 = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
    expectedBcryptMcfEncoded = $$"$2b$12$25BCOnh6F/QY89RGkU83H.qnIYintMZA9VQ3qWzqxEtvw4tXyO5Py",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$47DEQpj8HBSa+/TImW+5JA$SIccW0Qtg/lQJyg+5S5HaSe5hzUqpzlpT0FZHH24rpPHsvua5MjBldLxXVCGYztmsJtLiU7D7QZWzN/mlNKkvQ"
)

private suspend fun knownVector2() = createKnownVector(
    input = " ",
    expectedSha256 = "36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068",
    expectedSha512 = "f90ddd77e400dfe6a3fcf479b00b1ee29e7015c5bb8cd70f5f15b4886cc339275ff553fc8a053f8ddc7324f45168cffaf81f8c3ac93996f6536eef38e5e40768",
    expectedBcryptMcfEncoded = $$"$2b$12$Loll6ajZet83jyNevaRMjOQj5eZokAr/VoyT/B81gVcigGkXjv9e.",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$Nqnn8clbgv+5l0PgxcTOlQ$1vjyDAtqCH+KQPxO0mhBR4zbIoF2919kBVnLuECm8M8Kq+3vc9oyLS6InLO2whmP0CCgTcNd83yKBqZTfNpzYw"
)

private suspend fun knownVector3() = createKnownVector(
    input = "\n\n  ",
    expectedSha256 = "4308ef96ad0e86f35c795a177206056556333e814e65bfc9cd04bb164f8d61eb",
    expectedSha512 = "6d317431699988eec1fbb52dadfe5d0bfb14d38398470401e1648c412244ee3c90bdbf627b21c37faf1e7ef00e5de1b69555362028a37b471da328c3fac59941",
    expectedBcryptMcfEncoded = $$"$2b$12$OuhtjoyMftLacTmVaeWDXOEmLve0j.AXy74JiSBFzVjpov6SCkJlK",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$Qwjvlq0OhvNceVoXcgYFZQ$jNwuBTBCAMjy3anuNFnZZqwhBB6BNpF+1BFFMX8xpVIzXVdPSOEp0/3nR00KuzAJivSpCGORKRlotWhqvnLVbw"
)

private suspend fun knownVector4() = createKnownVector(
    input = "hello",
    expectedSha256 = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
    expectedSha512 = "9b71d224bd62f3785d96d46ad3ea3d73319bfbc2890caadae2dff72519673ca72323c3d99ba5c11d7c7acc6e14b8c5da0c4663475c2e5c3adef46f73bcdec043",
    expectedBcryptMcfEncoded = $$"$2b$12$JNHLsj8umu2k4BqovZlgleaWoo2BxGM0sAUbeJTsGdDCs24koovhq",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$LPJNul+wow4m6Dsqxbning$Jomso/aWbL3bXqb0Y+WuQOdMiveFYCBBC9FClw5pAihWocz0xFhc+Qns2PJZ3PhBp8doN1Eb9dyx36q1B50lBg"
)

private suspend fun knownVector5() = createKnownVector(
    input = "Hello World",
    expectedSha256 = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e",
    expectedSha512 = "2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b",
    expectedBcryptMcfEncoded = $$"$2b$12$nXEkz.tyGC/I.Paxx5cvi.OqWhN04gD3je48K05tPyPNL4w8snY0O",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$pZGm1Av0IEBKARczz7exkA$whbMNRoF5/S71AQakvFTqefPzAnppbAGVdGk3fKf9qselK/JGaY5fDawfqfVQ6hkTVWnMVoHzN7GiuwDz9a6/A",
)

internal suspend fun knownVectors() = listOf(
    knownVector1(),
    knownVector2(),
    knownVector3(),
    knownVector4(),
    knownVector5()
)


internal class KnownVector(
    val input: String,
    val expectedSha256: ByteArray,
    val expectedSha512: ByteArray,
    val expectedBcrypt: ByteArray,
    val expectedArgon2id: ByteArray
) {

    /**
     * @param length Set null for full-length
     */
    suspend fun getExpectedDigest(
        inputHasher: InputHasher,
        passEncoder: StringPassEncoder,
        length: Int?
    ): String {
        val binaryBlockSize: Int
        val encodedBlockSize: Int

        val bytes = when (inputHasher) {
            is InputHasher.ARGON2ID -> expectedArgon2id
            is InputHasher.BCrypt -> expectedBcrypt
            is InputHasher.SHA512 -> expectedSha512
            is InputHasher.SHA256 -> expectedSha256
        }

        when (passEncoder) {
            is StringPassEncoder.HexPassEncoder -> {
                binaryBlockSize = 1
                encodedBlockSize = 2
            }

            is StringPassEncoder.Base64PassEncoder -> {
                binaryBlockSize = 3
                encodedBlockSize = 4
            }

            is StringPassEncoder.Z85PassEncoder -> {
                binaryBlockSize = 4
                encodedBlockSize = 5
            }
        }

        val maxSize = bytes.size - (bytes.size % binaryBlockSize)
        val validBytes = bytes.copyOfRange(0, maxSize)
        val encoded = passEncoder.encodeAllBytes(validBytes)

        check(encoded.length == (maxSize / binaryBlockSize) * encodedBlockSize) {
            "Encoded size must be a multiple of encodedBlockSize!"
        }

        return if (length == null) encoded
        else encoded.substring(0, length)
    }
}