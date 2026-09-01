package ir.ornix.passgen.codec

class Utf8TextCodec : TextCodec {

    override fun decode(input: String): ByteArray {
        return input.encodeToByteArray()
    }

    override fun encode(input: ByteArray): String {
        return input.decodeToString()
    }
}