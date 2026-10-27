package ir.ornix.passgen.core.domain.credential

import ir.ornix.passgen.core.domain.CredentialRepository
import ir.ornix.passgen.core.model.Credential

class SaveCredentialUseCase(private val repository: CredentialRepository) {
    suspend operator fun invoke(credential: Credential) = repository.save(credential)
}
