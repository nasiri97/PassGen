package ir.ornix.passgen.core.domain.passgenconfig.kdf

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository

class RemoveKdfPassGenConfigUseCase(
    private val configRepo: PassGenConfigRepository,
    private val hmacSigner: HmacSigner
) {
    suspend operator fun invoke(configId: Int) {
        configRepo.removeById(configId)
        hmacSigner.deleteMasterKeyDigest("$configId")
    }
}