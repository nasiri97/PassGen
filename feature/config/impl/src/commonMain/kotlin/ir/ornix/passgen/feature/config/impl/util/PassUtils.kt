package ir.ornix.passgen.feature.config.impl.util

import ir.ornix.passgen.core.common.passwordgenerator.model.InputHasher
import ir.ornix.passgen.core.common.passwordgenerator.model.PassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.SeedPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.model.StringPassEncoder
import ir.ornix.passgen.core.common.passwordgenerator.random.RandomPassGen


internal val DEFAULT_INPUT_HASHER = InputHasher.ARGON2ID
internal val DEFAULT_PASS_ENCODER = StringPassEncoder.Z85PassEncoder
internal const val DEFAULT_PASS_LENGTH = 16

internal fun getValidPassLength(
    encoder: PassEncoder,
    maxAvailablePassLength: Int?,
    currentPassLength: Int
): Int {
    return when (encoder) {
        is SeedPassEncoder -> currentPassLength
        is StringPassEncoder -> {
            if (maxAvailablePassLength != null && currentPassLength > maxAvailablePassLength) maxAvailablePassLength
            else currentPassLength
        }
    }
}

internal fun getEntropySize(encoder: PassEncoder, passLength: Int): Int {
    return when (encoder) {
        is SeedPassEncoder -> encoder.entropyByteSize
        is StringPassEncoder -> encoder.getEntropyByteSize(passLength)
    }
}

internal fun getKdfMaxAvailablePassLength(hasher: InputHasher, encoder: PassEncoder): Int? {
    return when (encoder) {
        is SeedPassEncoder -> null
        is StringPassEncoder -> encoder.getTokenLength(byteSize = hasher.outputByteSize)
    }
}

internal fun getRandomMaxAvailablePassLength(encoder: PassEncoder): Int? {
    return when (encoder) {
        is SeedPassEncoder -> null
        is StringPassEncoder -> encoder.getTokenLength(byteSize = RandomPassGen.RANDOM_PASS_GEN_MAX_ENTROPY_BYTE_SIZE)
    }
}