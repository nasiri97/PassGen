package ir.ornix.passgen.core.domain.passgenconfig

import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.PassGenConfigRepository
import ir.ornix.passgen.core.domain.passgenconfig.model.KDFPassGenConfig


class AddPassGenConfigUseCase(
    private val passGenConfigRepo: PassGenConfigRepository,
    private val hmacSigner: HmacSigner
) {
    suspend operator fun invoke(config: KDFPassGenConfig, rawKey: ByteArray) {
        val configId = passGenConfigRepo.add(config)
        hmacSigner.registerKey("$configId", rawKey)
    }
}