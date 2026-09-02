package ir.ornix.passgen.hashing

import ir.ornix.passgen.codec.Base64BinaryCodec
import ir.ornix.passgen.codec.Utf8TextCodec
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals

class Argon2HashingTest {

    val base64Codec = Base64BinaryCodec()
    val utf8TextCodec = Utf8TextCodec()

    @Test
    fun testFunctionality() = runTest {
        // $argon2id$v=19$m=131072,t=4,p=1$47DEQpj8HBSa+/TImW+5JCeu$TZEUFRjR4XglkuGWigLExSHWkHeZPgGbZbTzc3GO8v+0s0Q2bUdKA5dIvT8enyLMEioyKap7V8RtFOPWe2duySNPTiXYLZoe
        val str1 = ""
        val digest1 =
            "TZEUFRjR4XglkuGWigLExSHWkHeZPgGbZbTzc3GO8v+0s0Q2bUdKA5dIvT8enyLMEioyKap7V8RtFOPWe2duySNPTiXYLZoe"

        // $argon2id$v=19$m=131072,t=4,p=1$Qwjvlq0OhvNceVoXcgYFZVYz$jxc0uewPv9Won/r+sPNVczuSzFgEUjta5Sl7OjqvOz/7m+jkaPStsIF3WFMS5s7PVnyL+ohUwU8Ziqig4w6W8M9DCM5D7yI9
        val str2 = "\n\n  "
        val digest2 =
            "jxc0uewPv9Won/r+sPNVczuSzFgEUjta5Sl7OjqvOz/7m+jkaPStsIF3WFMS5s7PVnyL+ohUwU8Ziqig4w6W8M9DCM5D7yI9"

        // $argon2id$v=19$m=131072,t=4,p=1$LPJNul+wow4m6DsqxbninhsW$fWJxxBMutLK9qCj/WsMCGQXjl2Qq9NeJtWge4K1pSHw/HOwouo0f7h5DAk/1nHim98UsH2JFESoALSiUCKszX2w41v/2nUX3
        val str3 = "hello"
        val digest3 =
            "fWJxxBMutLK9qCj/WsMCGQXjl2Qq9NeJtWge4K1pSHw/HOwouo0f7h5DAk/1nHim98UsH2JFESoALSiUCKszX2w41v/2nUX3"

        // $argon2id$v=19$m=131072,t=4,p=1$pZGm1Av0IEBKARczz7exkNYs$Foy0cX290UvEGXtJ7ZgHilwaKDRCietAg0QgYE+oTU+/qjWSOV9zsE/ScdADXCfzQWty+zay4A7bIROCcQgnE0ua1s309wZ1
        val str4 = "Hello World"
        val digest4 =
            "Foy0cX290UvEGXtJ7ZgHilwaKDRCietAg0QgYE+oTU+/qjWSOV9zsE/ScdADXCfzQWty+zay4A7bIROCcQgnE0ua1s309wZ1"

        assertContentEquals(
            base64Codec.decode(digest1),
            Argon2Hashing.digest(utf8TextCodec.decode(str1))
        )

        assertContentEquals(
            base64Codec.decode(digest2),
            Argon2Hashing.digest(utf8TextCodec.decode(str2))
        )

        assertContentEquals(
            base64Codec.decode(digest3),
            Argon2Hashing.digest(utf8TextCodec.decode(str3))
        )

        assertContentEquals(
            base64Codec.decode(digest4),
            Argon2Hashing.digest(utf8TextCodec.decode(str4))
        )
    }
}