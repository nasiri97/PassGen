package ir.ornix.passgen.core.common.hashing

import ir.ornix.passgen.core.common.codec.BCryptBase64BinaryCodec
import ir.ornix.passgen.core.common.codec.Base64BinaryCodec
import ir.ornix.passgen.core.common.codec.HexBinaryCodec
import ir.ornix.passgen.core.common.codec.Utf8TextCodec


private val base64Codec = Base64BinaryCodec()
private val bCryptBase64Codec = BCryptBase64BinaryCodec()
private val utf8Codec = Utf8TextCodec()
private val hexCodec = HexBinaryCodec(false)


private suspend fun knownVector1() = createKnownVector(
    inputBytes = ByteArray(0),
    expectedSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
    expectedSha512 = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
    expectedBcryptMcfEncoded = $$"$2a$12$25BCOnh6F/QY89RGkU83H.qnIYintMZA9VQ3qWzqxEtvw4tXyO5Py",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$47DEQpj8HBSa+/TImW+5JA$SIccW0Qtg/lQJyg+5S5HaSe5hzUqpzlpT0FZHH24rpPHsvua5MjBldLxXVCGYztmsJtLiU7D7QZWzN/mlNKkvQ"
)

private suspend fun knownVector2() = createKnownVector(
    inputBytes = hexCodec.decode("00"),
    expectedSha256 = "6e340b9cffb37a989ca544e6bb780a2c78901d3fb33738768511a30617afa01d",
    expectedSha512 = "b8244d028981d693af7b456af8efa4cad63d282e19ff14942c246e50d9351d22704a802a71c3580b6370de4ceb293c324a8423342557d4e5c38438f0e36910ee",
    expectedBcryptMcfEncoded = $$"$2b$12$ZhOJlN8xcnganSRks1eIJ.QyBT0dMyd4sYIeavoaacRnEUtpGy.M.",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$bjQLnP+zepicpUTmu3gKLA$pvEbvnWSbP6Bi6vBzsRRksk77aARRKWJ7J/HnU4HC8HT6YcjLWcH4DNMnyV3KjFMT5s5dJw8KyYwsXx/28UTlQ"
)

private suspend fun knownVector3() = createKnownVector(
    inputBytes = hexCodec.decode("00000000"),
    expectedSha256 = "df3f619804a92fdb4057192dc43dd748ea778adc52bc498ce80524c014b81119",
    expectedSha512 = "ec2d57691d9b2d40182ac565032054b7d784ba96b18bcb5be0bb4e70e3fb041eff582c8af66ee50256539f2181d7f9e53627c0189da7e75a4d5ef10ea93b20b3",
    expectedBcryptMcfEncoded = $$"$2b$12$1x7fk.QnJ7r.TvirvB1VQ.jWwDU/Nlqb0p4f1cGdsNWP3aYqy9oui",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$3z9hmASpL9tAVxktxD3XSA$5zRXagG29lNXHYl8Oeqgw3iNlK0ZF2lsKTuppPuMT7RfbD2125UZSd8dpT1L8Yc90VOQIpFPapZMwgH8AEgzsA"
)

private suspend fun knownVector4() = createKnownVector(
    inputBytes = hexCodec.decode("00123456"),
    expectedSha256 = "a4f01a4e3fc21d8d47b4ebc53dbccbfaa8ac316da628768ef5296f8d8d58f378",
    expectedSha512 = "5e77630cf75d16b05d5bc3466fce229e2bd5a6dc12d7d39bf9b4575bc375d11cfd51968fae509f9595598c2e9d629712307bebe962f86c723a6995fb3adeaf9f",
    expectedBcryptMcfEncoded = $$"$2b$12$nN.YRh9AFWzFrMtDNZxJ8e6WMqr0PHCC54DRx/8StFwpFA6NjYRUC",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$pPAaTj/CHY1HtOvFPbzL+g$JK8RtYkyGjWCXEJDQxqlbzYh1uMqKlSeJC+7RGJNMQQMbjXMV/Sv7IkuGLU73G3At320+7wSafLiGMuWxxU+Ww"
)

private suspend fun knownVector5() = createKnownVector(
    inputBytes = hexCodec.decode("12340056"),
    expectedSha256 = "b77a38f1a4104338bb15867993c507bd2a4fd53565a06376c8960b0fdc06ea74",
    expectedSha512 = "78085cdf34de31176c86f63293cd1d488d0e9d163ebb2c46127f64bac40f43d4ffc8937f59d1b0a4eb9b2cc060f2060547a4b3de14c7b696fdd4d3febd7610ad",
    expectedBcryptMcfEncoded = $$"$2b$12$r1m26YOOOxg5DWX3i6SFtOzuVI8ysXMEzE38PYaQ133KPyYz2JhHi",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$t3o48aQQQzi7FYZ5k8UHvQ$AfLH52KMe/3F87RGf+J3bpc+05u54MIhXZG3VRqrHZ3BpN/WJztqxiYlOENHztX00lXAyivQIEROoXBO4ROovw"
)

private suspend fun knownVector6() = createKnownVector(
    inputBytes = hexCodec.decode("1234567800"),
    expectedSha256 = "367119ea64bce8247631617b0f9fa9a3933aed64f0ef38e900c55d8c925c1e1b",
    expectedSha512 = "60951526f9bbc9a68c3a3f02aea7be433d7d8768f7415f6fa243e4e4acc4711ae9b90f5db084bdd653b8aee2b6675b2c7d379f0357b82fbe0e13d281d81a5f9a",
    expectedBcryptMcfEncoded = $$"$2b$12$LlCX4kQ64AP0KUD5B38nmuMVh3/69cyxJAQRIR2BIoIN9fwTT2Sla",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$NnEZ6mS86CR2MWF7D5+pow$1woN2P6DWTlVhpHa5KPVCI64+h0FT9GasloAvctP4iZbPwnZ+sOOdxUGqNlH0Ca3AaLfpelJZ1atGInbAtrdLQ"
)

private suspend fun knownVector7() = createKnownVector(
    inputBytes = utf8Codec.decode(" "),
    expectedSha256 = "36a9e7f1c95b82ffb99743e0c5c4ce95d83c9a430aac59f84ef3cbfab6145068",
    expectedSha512 = "f90ddd77e400dfe6a3fcf479b00b1ee29e7015c5bb8cd70f5f15b4886cc339275ff553fc8a053f8ddc7324f45168cffaf81f8c3ac93996f6536eef38e5e40768",
    expectedBcryptMcfEncoded = $$"$2b$12$Loll6ajZet83jyNevaRMjOQj5eZokAr/VoyT/B81gVcigGkXjv9e.",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$Nqnn8clbgv+5l0PgxcTOlQ$1vjyDAtqCH+KQPxO0mhBR4zbIoF2919kBVnLuECm8M8Kq+3vc9oyLS6InLO2whmP0CCgTcNd83yKBqZTfNpzYw"
)

private suspend fun knownVector8() = createKnownVector(
    inputBytes = utf8Codec.decode("\n\n  "),
    expectedSha256 = "4308ef96ad0e86f35c795a177206056556333e814e65bfc9cd04bb164f8d61eb",
    expectedSha512 = "6d317431699988eec1fbb52dadfe5d0bfb14d38398470401e1648c412244ee3c90bdbf627b21c37faf1e7ef00e5de1b69555362028a37b471da328c3fac59941",
    expectedBcryptMcfEncoded = $$"$2a$12$OuhtjoyMftLacTmVaeWDXOEmLve0j.AXy74JiSBFzVjpov6SCkJlK",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$Qwjvlq0OhvNceVoXcgYFZQ$jNwuBTBCAMjy3anuNFnZZqwhBB6BNpF+1BFFMX8xpVIzXVdPSOEp0/3nR00KuzAJivSpCGORKRlotWhqvnLVbw"
)

private suspend fun knownVector9() = createKnownVector(
    inputBytes = utf8Codec.decode("hello"),
    expectedSha256 = "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
    expectedSha512 = "9b71d224bd62f3785d96d46ad3ea3d73319bfbc2890caadae2dff72519673ca72323c3d99ba5c11d7c7acc6e14b8c5da0c4663475c2e5c3adef46f73bcdec043",
    expectedBcryptMcfEncoded = $$"$2a$12$JNHLsj8umu2k4BqovZlgleaWoo2BxGM0sAUbeJTsGdDCs24koovhq",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$LPJNul+wow4m6Dsqxbning$Jomso/aWbL3bXqb0Y+WuQOdMiveFYCBBC9FClw5pAihWocz0xFhc+Qns2PJZ3PhBp8doN1Eb9dyx36q1B50lBg"
)

private suspend fun knownVector10() = createKnownVector(
    inputBytes = utf8Codec.decode("Hello World"),
    expectedSha256 = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146e",
    expectedSha512 = "2c74fd17edafd80e8447b0d46741ee243b7eb74dd2149a0ab1b9246fb30382f27e853d8585719e0e67cbda0daa8f51671064615d645ae27acb15bfb1447f459b",
    expectedBcryptMcfEncoded = $$"$2a$12$nXEkz.tyGC/I.Paxx5cvi.OqWhN04gD3je48K05tPyPNL4w8snY0O",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$pZGm1Av0IEBKARczz7exkA$whbMNRoF5/S71AQakvFTqefPzAnppbAGVdGk3fKf9qselK/JGaY5fDawfqfVQ6hkTVWnMVoHzN7GiuwDz9a6/A"
)

private suspend fun knownVector11() = createKnownVector(
    inputBytes = utf8Codec.decode("Hello from Hashing\nWe try to hash your content!"),
    expectedSha256 = "2d589220278b9585b8140665bd70d95d46a9df75c9048542febfcd45c0feddbc",
    expectedSha512 = "bd2bdac92d5902008cd7473f669d30c32de7c1348d4c340f96bb97ee14c43c0de19f0d3b3f93f014a40285bb4bb6b4eec5214dc82396e0bb05fe36429c93a739",
    expectedBcryptMcfEncoded = $$"$2b$12$JTgQGAcJjWU2D.XjtVBXVOL683qOJaFaWUz0A.qai4kQJQDdajJNG",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$LViSICeLlYW4FAZlvXDZXQ$TGvo7s/QGEI1LPScbUEI4hzyeePvq+vgxtof8s25MgZEhpNHDHRYqW9l3S4Z3W+SBAWUR6I09L2fBMn9G83jtg"
)

private suspend fun knownVector12() = createKnownVector( // input: random 72 bytes
    inputBytes = hexCodec.decode("c7d15d8dbd9248c783662d174711e7c581333e3e2806897ed80d23e3cb1e714c54dca45a7682fe37d2fdc260deb9ec7c6ffc20ca78864c6744e716005600447a"),
    expectedSha256 = "37f99c6a0caf27fbcd853101f5d8d42b1578318ba51b4992de53b6368d3f421e",
    expectedSha512 = "33fd202ee6f97662803d4c3596956d1eea181802676882eb85d1a817132227eabfea61cb4c32e122efc985afe364299bc6993c4a7a5ab3c603b30f4f6d78a164",
    expectedBcryptMcfEncoded = $$"$2b$12$L9kaYewtH9tLfRC/7bhSIu4SBA3BI.4rz3y/h.mK06Vj/9RL3mZzO",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$N/mcagyvJ/vNhTEB9djUKw$iqcZbIvWdBlgfcsMqqMPokrig0oCuMz7XR6eYzCi2S2F4nos50HADyMkq5d5TcLC0+XM997OiU0EWH6CbzqJeg"
)

private suspend fun knownVector13() = createKnownVector( // input: random 72 bytes
    inputBytes = hexCodec.decode("fcb75e8d004061c9fd9c948bd800d3caedea6b721bf475aed74a926b0fe41bf6bbafc9ab6109bdeb7ced9a5f21e1cc17d61c4fa7eb91ed18584f3511c273acc8"),
    expectedSha256 = "bd114a08fd3f36b43dcd7a6f13c4e005f7ecaf4cd7a8a21f598aa7017b8629ee",
    expectedSha512 = "c13e8fad03f030c7e5cf0be02204751242772f9ca369ece1f5043ef0ff49d89adc4880c3494a8dc19ea17f04d789c2eb6a04dcbe45e47bac0479308a1cba66e4",
    expectedBcryptMcfEncoded = $$"$2b$12$tPDIANy9LpO7xVntC6Re/Ohp4K0BL1jeiB0olkQ.s5sme/xp6i42K",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$vRFKCP0/NrQ9zXpvE8TgBQ$QeZ32H9jV613SUXHBJcS07Er2ejcCAHCqdT3R572ixu4XROKbrrnw27Xlg+Q92LDWeclLRJB5RvMoE+nrF1CoQ"
)

private suspend fun knownVector14() = createKnownVector( // input: random 72 bytes
    inputBytes = hexCodec.decode("1a8eb975709c30d365b67244966072eac6f8a369ef7029fcb9677c43e7c327d356e64ae15bc5c03ba96f5ff9b56d4bf92f73a10e87f510119f5949ec0c9feb89"),
    expectedSha256 = "49ba210e11118c0a712ab8e17dc19af5dc73c4b21d28be253a206410604a3373",
    expectedSha512 = "b4c2ce3897bf861f3722014f1d2d4ec1a54fa15d080e57962fc99e8d24e12da0b6ef42766f9c19670d8bceb64c50c083c03b7db03759b0f86bd9715c28b76637",
    expectedBcryptMcfEncoded = $$"$2b$12$QZmfBfCPh.nvIphfdaEY7O/dSESQHXx41eDPV7XpUZawCW3rFmOx6",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$SbohDhERjApxKrjhfcGa9Q$9QEiT577XaMzqlwPHfrIfcukmzj/7qv1h7iaORRpncoylLziXax0j5B0gjOiTYH+TGUO8qkv1cDSznhm6cGBZQ"
)

private suspend fun knownVector15() = createKnownVector( // input: random 72 bytes
    inputBytes = hexCodec.decode("8a23e897481aff2e8e07c98d6379bebb35c951f0b4d33d802eb66c39a9b39eab337e12674c4093ab48860d169916e1aaeea13006bcaacb45dc9e538e93965f2a7135fcf467fb94b8"),
    expectedSha256 = "2373bc1983fcbfed7bc938a731328b2f5c0f7df83b58e9bcbd3fefc68e83e5b1",
    expectedSha512 = "2fa90bc1403b2b0b5279d8d1470a0885840d2fdd6bf561c4d16f26350bbba975b85e0c1e8a808066ca655a312eb0f02a137096715118fad45a19d120f034aeb8",
    expectedBcryptMcfEncoded = $$"$2b$12$G1M6EWN6t8z5wRglKRIJJuTPW.RwyqnprVbHB67/7HClKS9AbzDSC",
    expectedArgon2idPhcEncoded = $$"$argon2id$v=19$m=131072,t=4,p=1$I3O8GYP8v+17yTinMTKLLw$b8Z5kxebd6zoqTEPPBX7sP0gPGbuNTmOc+y2JFrhucj+snBXNvsWCTkrSqqOHsH8xVw5tUEYjI7L/FMMP6RKRQ"
)

internal suspend fun knownVectors() = listOf(
    knownVector1(),
    knownVector2(),
    knownVector3(),
    knownVector4(),
    knownVector5(),
    knownVector6(),
    knownVector7(),
    knownVector8(),
    knownVector9(),
    knownVector10(),
    knownVector11(),
    knownVector12(),
    knownVector13(),
    knownVector14(),
    knownVector15()
)

private suspend fun createKnownVector(
    inputBytes: ByteArray,
    expectedSha256: String,
    expectedSha512: String,
    expectedBcryptMcfEncoded: String,
    expectedArgon2idPhcEncoded: String
) = KnownVector(
    inputBytes = inputBytes,
    expectedSha256 = hexCodec.decode(expectedSha256),
    expectedSha512 = hexCodec.decode(expectedSha512),
    expectedBcrypt = bCryptBase64Codec.decode(
        expectedBcryptMcfEncoded.substring(29)
    ),
    expectedBcryptMcfEncoded = expectedBcryptMcfEncoded,
    expectedArgon2id = base64Codec.decode(
        expectedArgon2idPhcEncoded.substring(expectedArgon2idPhcEncoded.lastIndexOf('$') + 1)
    ),
    expectedArgon2idPhcEncoded = expectedArgon2idPhcEncoded
)


internal class KnownVector(
    val inputBytes: ByteArray,
    val expectedSha256: ByteArray,
    val expectedSha512: ByteArray,
    val expectedBcrypt: ByteArray,
    val expectedBcryptMcfEncoded: String,
    val expectedArgon2id: ByteArray,
    val expectedArgon2idPhcEncoded: String
)