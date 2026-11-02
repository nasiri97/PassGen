package ir.ornix.passgen.core.domain.data_management

import ir.ornix.passgen.core.domain.CredentialRepository
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.PassGenConfigRepository

class PurgeSensitiveDataUseCase(
    private val hmacSigner: HmacSigner,
    private val passGenConfigRepository: PassGenConfigRepository,
    private val credentialRepository: CredentialRepository,
    private val localAuthRepository: LocalAuthRepository
) {
    suspend operator fun invoke() {
        // Delete all MKDs
        passGenConfigRepository.getAllKdfPassGenConfigIds().forEach { kdfConfigId ->
            hmacSigner.deleteMasterKeyDigest("$kdfConfigId")
        }

        passGenConfigRepository.deleteAllPassGenConfigs()
        credentialRepository.deleteAllCredentials()
        localAuthRepository.clearAuth()
    }
}