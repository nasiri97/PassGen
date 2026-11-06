package ir.ornix.passgen.core.domain.passgenconfig.kdf

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.KdfPassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig

class AddKdfPassGenConfigUseCase(
    private val repo: KdfPassGenConfigRepository,
    private val hmacSigner: HmacSigner
) {
    suspend operator fun invoke(
        config: KdfPassGenConfig,
        masterKey: CharArray
    ): Boolean {

        val isKeyRegistered = hmacSigner.registerKey(
            mkdId = config.masterKeyDigestId,
            rawMasterKey = masterKey.concatToString().encodeToByteArray()
        )

        return if (isKeyRegistered) {
            repo.addConfig(config)
            true
        } else false
    }
}