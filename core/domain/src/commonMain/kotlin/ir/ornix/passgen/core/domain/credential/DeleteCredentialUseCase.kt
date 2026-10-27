package ir.ornix.passgen.core.domain.credential

import ir.ornix.passgen.core.domain.CredentialRepository

class DeleteCredentialUseCase(private val repository: CredentialRepository) {
    suspend operator fun invoke(credentialId: Long) = repository.delete(credentialId)
}
