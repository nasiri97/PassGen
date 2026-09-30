package ir.ornix.passgen.core.domain.passgenconfig.kdf

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.model.passgenconfig.KdfPassGenConfig


class AddKdfPassGenConfigUseCase(
    private val passGenConfigRepo: PassGenConfigRepository,
    private val hmacSigner: HmacSigner
) {
    suspend operator fun invoke(config: KdfPassGenConfig, rawKey: ByteArray) {
        val configId = passGenConfigRepo.add(config)
        hmacSigner.registerKey("$configId", rawKey)
    }
}