package ir.ornix.passgen.core.data

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.SigningKeyNotFoundException

actual class SecureHmacSigner : HmacSigner {

    private val masterKeyDigests = mutableMapOf<String, ByteArray>()

    actual override suspend fun registerKey(mkdId: String, rawMasterKey: ByteArray) {
        val hashedKey = sha512(rawMasterKey)
        try {
            masterKeyDigests[mkdId] = hashedKey
        } finally {
            rawMasterKey.fill(0)
        }
    }

    actual override suspend fun sign(mkdId: String, input: String): ByteArray {
        val hashedKey = masterKeyDigests[mkdId] ?: throw SigningKeyNotFoundException(mkdId)
        return hmacSha512(key = hashedKey, message = input.encodeToByteArray())
    }

    actual override suspend fun hasMasterKeyDigest(mkdId: String): Boolean =
        masterKeyDigests.containsKey(mkdId)

    actual override suspend fun deleteMasterKeyDigest(mkdId: String) {
        val removedKey = masterKeyDigests.remove(mkdId)
        removedKey?.fill(0)
    }

    private companion object {
        private const val BLOCK_SIZE = 128
        private const val DIGEST_SIZE = 64

        private val H_INIT = longArrayOf(
            0x6a09e667f3bcc908L,
            0xbb67ae8584caa73bUL.toLong(),
            0x3c6ef372fe94f82bL,
            0xa54ff53a5f1d36f1UL.toLong(),
            0x510e527fade682d1L,
            0x9b05688c2b3e6c1fUL.toLong(),
            0x1f83d9abfb41bd6bL,
            0x5be0cd19137e2179L
        )

        private val K = longArrayOf(
            0x428a2f98d728ae22L,
            0x7137449123ef65cdL,
            0xb5c0fbcfec4d3b2fUL.toLong(),
            0xe9b5dba58189dbbcUL.toLong(),
            0x3956c25bf348b538L,
            0x59f111f1b605d019L,
            0x923f82a4af194f9bUL.toLong(),
            0xab1c5ed5da6d8118UL.toLong(),
            0xd807aa98a3030242UL.toLong(),
            0x12835b0145706fbeL,
            0x243185be4ee4b28cL,
            0x550c7dc3d5ffb4e2L,
            0x72be5d74f27b896fL,
            0x80deb1fe3b1696b1UL.toLong(),
            0x9bdc06a725c71235UL.toLong(),
            0xc19bf174cf692694UL.toLong(),
            0xe49b69c19ef14ad2UL.toLong(),
            0xefbe4786384f25e3UL.toLong(),
            0x0fc19dc68b8cd5b5L,
            0x240ca1cc77ac9c65L,
            0x2de92c6f592b0275L,
            0x4a7484aa6ea6e483L,
            0x5cb0a9dcbd41fbd4L,
            0x76f988da831153b5L,
            0x983e5152ee66dfabUL.toLong(),
            0xa831c66d2db43210UL.toLong(),
            0xb00327c898fb213fUL.toLong(),
            0xbf597fc7beef0ee4UL.toLong(),
            0xc6e00bf33da88fc2UL.toLong(),
            0xd5a79147930aa725UL.toLong(),
            0x06ca6351e003826fL,
            0x142929670a0e6e70L,
            0x27b70a8546d22ffcL,
            0x2e1b21385c26c926L,
            0x4d2c6dfc5ac42aedL,
            0x53380d139d95b3dfL,
            0x650a73548baf63deL,
            0x766a0abb3c77b2a8L,
            0x81c2c92e47edaee6UL.toLong(),
            0x92722c851482353bUL.toLong(),
            0xa2bfe8a14cf10364UL.toLong(),
            0xa81a664bbc423001UL.toLong(),
            0xc24b8b70d0f89791UL.toLong(),
            0xc76c51a30654be30UL.toLong(),
            0xd192e819d6ef5218UL.toLong(),
            0xd69906245565a910UL.toLong(),
            0xf40e35855771202aUL.toLong(),
            0x106aa07032bbd1b8L,
            0x19a4c116b8d2d0c8L,
            0x1e376c085141ab53L,
            0x2748774cdf8eeb99L,
            0x34b0bcb5e19b48a8L,
            0x391c0cb3c5c95a63L,
            0x4ed8aa4ae3418acbL,
            0x5b9cca4f7763e373L,
            0x682e6ff3d6b2b8a3L,
            0x748f82ee5defb2fcL,
            0x78a5636f43172f60L,
            0x84c87814a1f0ab72UL.toLong(),
            0x8cc702081a6439ecUL.toLong(),
            0x90befffa23631e28UL.toLong(),
            0xa4506cebde82bde9UL.toLong(),
            0xbef9a3f7b2c67915UL.toLong(),
            0xc67178f2e372532bUL.toLong(),
            0xca273eceea26619cUL.toLong(),
            0xd186b8c721c0c207UL.toLong(),
            0xeada7dd6cde0eb1eUL.toLong(),
            0xf57d4f7fee6ed178UL.toLong(),
            0x06f067aa72176fbaL,
            0x0a637dc5a2c898a6L,
            0x113f9804bef90daeL,
            0x1b710b35131c471bL,
            0x28db77f523047d84L,
            0x32caab7b40c72493L,
            0x3c9ebe0a15c9bebcL,
            0x431d67c49c100d4cL,
            0x4cc5d4becb3e42b6L,
            0x597f299cfc657e2aL,
            0x5fcb6fab3ad6faecL,
            0x6c44198c4a475817L
        )

        private fun rotr(x: Long, n: Int): Long = (x ushr n) or (x shl (64 - n))
        private fun ch(x: Long, y: Long, z: Long): Long = (x and y) xor (x.inv() and z)
        private fun maj(x: Long, y: Long, z: Long): Long = (x and y) xor (x and z) xor (y and z)
        private fun bigSigma0(x: Long): Long = rotr(x, 28) xor rotr(x, 34) xor rotr(x, 39)
        private fun bigSigma1(x: Long): Long = rotr(x, 14) xor rotr(x, 18) xor rotr(x, 41)
        private fun smallSigma0(x: Long): Long = rotr(x, 1) xor rotr(x, 8) xor (x ushr 7)
        private fun smallSigma1(x: Long): Long = rotr(x, 19) xor rotr(x, 61) xor (x ushr 6)

        private fun sha512(data: ByteArray): ByteArray {
            val len = data.size.toLong()
            val bitLen = len * 8L

            val remainder = (len + 1 + 16) % BLOCK_SIZE
            val padLen = if (remainder == 0L) 0 else BLOCK_SIZE - remainder
            val paddedLen = (len + 1 + padLen + 16).toInt()

            val padded = ByteArray(paddedLen)
            data.copyInto(padded, 0, 0, data.size)
            padded[data.size] = 0x80.toByte()

            for (i in 0 until 8) {
                padded[paddedLen - 8 + i] = ((bitLen ushr (56 - i * 8)) and 0xFFL).toByte()
            }

            val h = LongArray(8) { H_INIT[it] }
            val w = LongArray(80)

            for (chunk in 0 until paddedLen step BLOCK_SIZE) {
                for (i in 0 until 16) {
                    val idx = chunk + i * 8
                    w[i] = ((padded[idx].toLong() and 0xFFL) shl 56) or
                            ((padded[idx + 1].toLong() and 0xFFL) shl 48) or
                            ((padded[idx + 2].toLong() and 0xFFL) shl 40) or
                            ((padded[idx + 3].toLong() and 0xFFL) shl 32) or
                            ((padded[idx + 4].toLong() and 0xFFL) shl 24) or
                            ((padded[idx + 5].toLong() and 0xFFL) shl 16) or
                            ((padded[idx + 6].toLong() and 0xFFL) shl 8) or
                            (padded[idx + 7].toLong() and 0xFFL)
                }

                for (i in 16 until 80) {
                    w[i] = smallSigma1(w[i - 2]) + w[i - 7] + smallSigma0(w[i - 15]) + w[i - 16]
                }

                var a = h[0]
                var b = h[1]
                var c = h[2]
                var d = h[3]
                var e = h[4]
                var f = h[5]
                var g = h[6]
                var hVal = h[7]

                for (i in 0 until 80) {
                    val t1 = hVal + bigSigma1(e) + ch(e, f, g) + K[i] + w[i]
                    val t2 = bigSigma0(a) + maj(a, b, c)

                    hVal = g
                    g = f
                    f = e
                    e = d + t1
                    d = c
                    c = b
                    b = a
                    a = t1 + t2
                }

                h[0] += a
                h[1] += b
                h[2] += c
                h[3] += d
                h[4] += e
                h[5] += f
                h[6] += g
                h[7] += hVal
            }

            padded.fill(0)
            w.fill(0)

            val digest = ByteArray(DIGEST_SIZE)
            for (i in 0 until 8) {
                val v = h[i]
                val idx = i * 8
                digest[idx] = (v ushr 56).toByte()
                digest[idx + 1] = (v ushr 48).toByte()
                digest[idx + 2] = (v ushr 40).toByte()
                digest[idx + 3] = (v ushr 32).toByte()
                digest[idx + 4] = (v ushr 24).toByte()
                digest[idx + 5] = (v ushr 16).toByte()
                digest[idx + 6] = (v ushr 8).toByte()
                digest[idx + 7] = v.toByte()
            }
            return digest
        }

        private fun hmacSha512(key: ByteArray, message: ByteArray): ByteArray {
            val keyBlock = ByteArray(BLOCK_SIZE)
            if (key.size > BLOCK_SIZE) {
                val hashedKey = sha512(key)
                hashedKey.copyInto(keyBlock, 0, 0, hashedKey.size)
                hashedKey.fill(0)
            } else {
                key.copyInto(keyBlock, 0, 0, key.size)
            }

            val ipad = ByteArray(BLOCK_SIZE)
            val opad = ByteArray(BLOCK_SIZE)

            for (i in 0 until BLOCK_SIZE) {
                ipad[i] = (keyBlock[i].toInt() xor 0x36).toByte()
                opad[i] = (keyBlock[i].toInt() xor 0x5c).toByte()
            }

            val innerMsg = ByteArray(BLOCK_SIZE + message.size)
            ipad.copyInto(innerMsg, 0, 0, BLOCK_SIZE)
            message.copyInto(innerMsg, BLOCK_SIZE, 0, message.size)
            val innerHash = sha512(innerMsg)

            val outerMsg = ByteArray(BLOCK_SIZE + DIGEST_SIZE)
            opad.copyInto(outerMsg, 0, 0, BLOCK_SIZE)
            innerHash.copyInto(outerMsg, BLOCK_SIZE, 0, DIGEST_SIZE)
            val mac = sha512(outerMsg)

            keyBlock.fill(0)
            ipad.fill(0)
            opad.fill(0)
            innerMsg.fill(0)
            innerHash.fill(0)
            outerMsg.fill(0)

            return mac
        }
    }
}
