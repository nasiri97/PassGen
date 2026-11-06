package ir.ornix.passgen.core.domain.passgen

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.KdfPassGenConfigRepository
import ir.ornix.passgen.core.domain.SigningKeyNotFoundException
import ir.ornix.passgen.core.logging.Logger
import ir.ornix.passgen.core.model.Password
import ir.ornix.passgen.core.model.Password.Companion.toPassword
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig

class GenerateKDFPassUseCase(
    private val repo: KdfPassGenConfigRepository,
    private val hmacSigner: HmacSigner
) {

    suspend operator fun invoke(config: KdfPassGenConfig, contextInput: String): Password? {
        val passGen = config.createPassGen()
        val processedContextInput = config.preprocessConfig(contextInput)

        return try {
            val passwordSeed = hmacSigner.sign(
                mkdId = config.masterKeyDigestId,
                input = processedContextInput
            )

            val password = passGen.generate(passwordSeed)
                ?.toPassword(entropyByteSize = config.entropyByteSize)

            passwordSeed.fill(0)
            password
        } catch (e: SigningKeyNotFoundException) {
            Logger.e(
                "Failed to generate KDF password: no MKD is registered for mkdId = ${config.masterKeyDigestId}.",
                e
            )
            repo.removeConfigById(config.id)
            null
        }
    }
}
