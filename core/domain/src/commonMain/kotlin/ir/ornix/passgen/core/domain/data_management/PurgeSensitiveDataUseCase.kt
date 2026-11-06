package ir.ornix.passgen.core.domain.data_management

import ir.ornix.passgen.core.domain.CredentialRepository
import ir.ornix.passgen.core.domain.HmacSigner
import ir.ornix.passgen.core.domain.KdfPassGenConfigRepository
import ir.ornix.passgen.core.domain.LocalAuthRepository
import ir.ornix.passgen.core.domain.RandomPassGenConfigRepository

class PurgeSensitiveDataUseCase(
    private val hmacSigner: HmacSigner,
    private val kdfConfigRepository: KdfPassGenConfigRepository,
    private val randomConfigRepository: RandomPassGenConfigRepository,
    private val credentialRepository: CredentialRepository,
    private val localAuthRepository: LocalAuthRepository
) {
    suspend operator fun invoke() {
        // Delete all MKDs
        kdfConfigRepository.getAllMasterKeyDigestIds().forEach { mkdId ->
            hmacSigner.deleteMasterKeyDigest(mkdId)
        }

        kdfConfigRepository.deleteAllConfigs()
        randomConfigRepository.deleteAllConfigs()
        credentialRepository.deleteAllCredentials()
        localAuthRepository.clearAuth()
    }
}