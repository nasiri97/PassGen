package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository

class RemoveKDFPassGenConfigUseCase(
    private val configRepo: PassGenConfigRepository,
    private val hmacSigner: HmacSigner
) {
    suspend operator fun invoke(configId: Int) {
        configRepo.removeById(configId)
        hmacSigner.deleteMasterKeyDigest("$configId")
    }
}