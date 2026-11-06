package ir.ornix.passgen.core.domain.passgenconfig.kdf

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.KdfPassGenConfigRepository

class RemoveKdfPassGenConfigUseCase(
    private val repo: KdfPassGenConfigRepository,
    private val hmacSigner: HmacSigner
) {
    suspend operator fun invoke(configId: Long) {
        val mkdId = repo.getConfigById(configId)?.masterKeyDigestId
        mkdId?.let { hmacSigner.deleteMasterKeyDigest(it) }
        repo.removeConfigById(configId)
    }
}
