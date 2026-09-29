package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.SigningKeyNotFoundException
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig
import ir.ornix.passgen.core.logging.Logger
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.Password.Companion.toPassword

class GenerateKDFPassUseCase(
    private val configRepo: PassGenConfigRepository,
    private val hmacSigner: HmacSigner
) {

    suspend operator fun invoke(config: KDFPassGenConfig, input: String): Password? {
        val passGen = config.createPassGen()
        val processedInput = config.preprocessConfig(input)

        return try {
            val passwordSeed = hmacSigner.sign(mkdId = "${config.id}", processedInput)

            val password =
                passGen.generate(passwordSeed)?.toPassword(entropyByteSize = config.entropyByteSize)

            passwordSeed.fill(0)
            password
        } catch (e: SigningKeyNotFoundException) {
            Logger.e(
                "Failed to generate KDF password: no MKD is registered for mkdId = ${config.id}.",
                e
            )
            configRepo.removeById(config.id)
            null
        }
    }
}
