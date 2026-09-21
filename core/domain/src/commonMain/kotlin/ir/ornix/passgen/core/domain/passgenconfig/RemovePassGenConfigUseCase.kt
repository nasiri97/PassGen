package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository

class RemovePassGenConfigUseCase(
    private val repo: PassGenConfigRepository,
    private val hmacSigner: HmacSigner
) {
    suspend operator fun invoke(configId: Int) {
        repo.removeById(configId)
        hmacSigner.deleteMasterKeyDigest("$configId")
    }
}